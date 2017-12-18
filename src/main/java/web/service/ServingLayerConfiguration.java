package web.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import web.scraping.jsoup.ArticlesScrapper;

@ComponentScan(basePackages = { "web.service" })
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class ServingLayerConfiguration {

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private static ScheduledFuture<?> batchHandler;
	private static long prevTotal;
    private static long prevFree;
	
	public static void main(String[] args) {
		
		
		final Runnable batch = new Runnable() {
			
			@Override
			public void run() {
				try {
					ArticlesScrapper.main(args);
				} catch (Exception e) {
					System.err.println("Cannot do the articles scrapper");
					e.printStackTrace();
				} 
			}
		};
		
		batchHandler = scheduler.scheduleAtFixedRate(batch, 1, 12, TimeUnit.HOURS);
		
		final Runnable monitor = new Runnable() {
			
			@Override
			public void run() {
				System.out.println("Time to check applications");
				System.out.println("The system is runing with " + Thread.activeCount() + " threads");
				Runtime rt = Runtime.getRuntime();
				long total = rt.totalMemory();
		        long free = rt.freeMemory();
				long used = total - free;
	            long prevUsed = (prevTotal - prevFree);
	            System.out.println("Total Memory: " + total + ", Used: " + used + ", Free: " + free + ", Used since last: " + (used - prevUsed) + ", Free since last: " + (free - prevFree));
	            prevTotal = total;
	            prevFree = free;
				long delay = batchHandler.getDelay(TimeUnit.HOURS);
				if (!batchHandler.isCancelled() && delay > -3L) {
					System.out.println("The batch is working");
				} else {
					System.out.println("The batch is not working :(");
					System.out.println("Restarting...");
					batchHandler.cancel(true);
					batchHandler = scheduler.scheduleAtFixedRate(batch, 1, 12, TimeUnit.HOURS);
					System.out.println("Restarted!");
				}
				if (delay > 0L) {
					System.out.println("Next batch execution in " + delay + " hours");
				} else {
					delay = batchHandler.getDelay(TimeUnit.MINUTES);
					System.out.println("Next batch execution in " + delay + " minutes");
				}
				System.out.println("System checked!");
			}
		};
		
		prevTotal = 0;
	    prevFree = Runtime.getRuntime().freeMemory();
		
		final ScheduledFuture<?> monitorHandler = scheduler.scheduleAtFixedRate(monitor, 1, 60, TimeUnit.MINUTES);
		
		
		SpringApplication.run(ServingLayerConfiguration.class, args);
	}

}