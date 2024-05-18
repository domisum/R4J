package no.stelar7.api.r4j.basic.ratelimiting;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Burst ratelimiter will use as many calls as possible, then wait when it reaches the limit
 */
public class BurstRateLimiter extends RateLimiter
{
	
	private static final Logger logger = LoggerFactory.getLogger(BurstRateLimiter.class);
	
	private ReentrantLock lock = new ReentrantLock();
	
	public BurstRateLimiter(List<RateLimit> limits)
	{
		super(limits.toArray(new RateLimit[0]));
	}
	
	@Override
	public void acquire()
	{
		lock.lock();
		try
		{
			var now = Instant.now();
			update(now);
			long sleepTime = getDelay(now);
			if(sleepTime > 0)
			{
				Duration dur = Duration.of(sleepTime, ChronoUnit.MILLIS);
				logger.info("Rate limiting activated! Sleeping for: {}", dur);
				Thread.sleep(sleepTime);
			}
		}
		catch(InterruptedException ignored) {}
		finally
		{
			lock.unlock();
		}
	}
	
	@Override
	public void updatePermitsTakenPerX(Map<Integer, Integer> data)
	{
		for(var entry : data.entrySet())
			for(var rateLimit : limits)
				if(rateLimit.getTimeframeInMS() / 1000 == entry.getKey())
				{
					long oldVal = callCountInTime.get(rateLimit).get();
					long newVal = entry.getValue();
					if(oldVal + 1 < newVal)
					{
						callCountInTime.get(rateLimit).set(newVal);
						logger.debug("limit {} has changed from {} to {}", entry, oldVal, newVal);
					}
				}
	}
	
	private long getDelay(Instant now)
	{
		if(overloadTimer > 0)
			logger.warn("Overload timer set to {}", overloadTimer);
		long delay = overloadTimer * 1000L;
		overloadTimer = 0;
		
		for(var limit : limits)
		{
			long actualCallCount = callCountInTime.get(limit).get();
			if(actualCallCount >= limit.getPermits())
			{
				logger.debug("Calls made in the time frame: {}", actualCallCount);
				logger.debug("Limit for the time frame: {}", limit.getPermits());
				
				long newDelay = firstCallInTime.get(limit).get() - now.toEpochMilli() + limit.getTimeframeInMS();
				newDelay += 500;
				
				if(newDelay > 10 * 1000)
				{
					logger.warn("Long delay: {} ({})", newDelay, limit);
					newDelay = 10 * 1000;
				}
				
				if(newDelay > delay)
					delay = newDelay;
			}
		}
		
		return delay;
	}
	
	private void update(Instant now)
	{
		for(var limit : limits)
		{
			var firstCall = firstCallInTime.computeIfAbsent(limit, (key) -> new AtomicLong(0));
			var callCount = callCountInTime.computeIfAbsent(limit, (key) -> new AtomicLong(0));
			
			if(firstCall.get() - now.toEpochMilli() + limit.getTimeframeInMS() <= 0)
			{
				firstCall.set(now.toEpochMilli());
				callCount.set(0);
			}
			
			callCount.incrementAndGet();
			logger.debug("{}: current call count: {}", limit, callCount);
		}
	}
	
}