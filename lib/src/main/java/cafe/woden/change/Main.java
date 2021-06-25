
package cafe.woden.change;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Main
{
	private static SecureRandom totalRandom;

	private static SecureRandom shuffleRandom;

	private static SecureRandom standardRandom;

	private static SecureRandom getStandardRandom()
	{
		try
		{
			if ( standardRandom == null )
			{
				standardRandom = SecureRandom.getInstanceStrong();
			}
			return standardRandom;
		}
		catch ( Throwable th )
		{
			throw new RuntimeException( th );
		}

	}

	private static SecureRandom getShuffleRandom()
	{
		try
		{
			if ( shuffleRandom == null )
			{
				shuffleRandom = SecureRandom.getInstanceStrong();
				shuffleRandom.setSeed( Util.getRandomNumber()
					.longValue() );
			}
			return shuffleRandom;
		}
		catch ( Throwable th )
		{
			throw new RuntimeException( th );
		}

	}

	private static int getTotalValue()
	{
		if ( totalRandom == null )
		{
			try
			{
				totalRandom = SecureRandom.getInstanceStrong();
				totalRandom.setSeed( Util.getRandomNumber()
					.longValue() );
			}
			catch ( NoSuchAlgorithmException e )
			{
				throw new RuntimeException( e );
			}
		}
		return totalRandom.ints( 100, 200 )
			.findAny()
			.getAsInt();
	}

	public static void main( String[] args )
	{
		getOutput();
		//System.out.println( "Coin: " + coin.toString() + " Count: " + coins.size() + " Value: " + totalValue );
		//writeOutput( Paths.get( System.getProperty( "user.home" ), "pocketchange10K.txt" ), 20000 );
	}

	private static void writeOutput( Path path, int number )
	{
		try
		{
			if ( Files.exists( path ) )
			{
				Files.delete( path );
			}

		}
		catch ( Throwable th )
		{
			throw new RuntimeException( th );
		}

		try
		{
			if ( !Files.exists( path ) )
			{
				Files.createFile( path );
			}
			AtomicInteger x = new AtomicInteger();
			IntStream.generate( Main::getOutput )
				.limit( number )
				.forEach( output -> {
					try
					{
						byte[] bytesToWrite = (String.valueOf( output ) + ((x.get() + 1) == number ? "" : System.lineSeparator()))
							.getBytes();
						Files.write( path, bytesToWrite, StandardOpenOption.APPEND );
					}
					catch ( IOException e )
					{
						throw new RuntimeException( e );
					}
					x.incrementAndGet();
				} );

			Files.write( path, System.lineSeparator()
				.getBytes(), StandardOpenOption.APPEND );
		}
		catch ( Throwable th )
		{
			throw new RuntimeException( th );
		}
	}

	private static int getOutput()
	{

		Collection<Coin> tailsCoins = new ArrayList<>();
		Collection<Coin> allCoins = Coin.getTotalValue( getTotalValue() );
		for ( Coin coin : allCoins )
		{
			coin = shuffleCoin( coin );
			if ( !coin.isHeads() )
			{
				tailsCoins.add( coin );
			}
		}
		int totalCoinValue = 0;
		for ( Coin coin : Coin.values() )
		{
			Collection<Coin> coins = new ArrayList<>();
			for ( Coin tailsCoin : tailsCoins )
			{
				if ( coin == tailsCoin )
				{
					coins.add( coin );
				}
			}
			int totalValue = coins.stream()
				.map( Coin::getValue )
				.reduce( 0, Integer::sum );
			System.out.println( "Coin: " + coin.toString() + " Count: " + coins.size() + " Value: " + totalValue );
			totalCoinValue += totalValue;

		}
		double totalCoinValueDouble;
		if ( totalCoinValue > 0 )
		{
			totalCoinValueDouble = totalCoinValue * 1.0 / 100;
		}
		else
		{
			totalCoinValueDouble = 0;
		}
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		String moneyString = formatter.format( totalCoinValueDouble );
		System.out.println( "Total value: " + moneyString );
		return totalCoinValue;

	}

	private static Coin shuffleCoin( Coin coin )
	{
		try
		{

			int shuffleSize = getShuffleRandom().ints( getStandardRandom().ints( 1, 100 )
				.findAny()
				.getAsInt(),
				getStandardRandom().ints( 100, 1000 )
					.findAny()
					.getAsInt() )
				.findAny()
				.getAsInt();
			for ( int x = 0; x < shuffleSize; x++ )
			{
				coin.flip();
			}
			return coin;
		}
		catch ( Throwable th )
		{
			throw new RuntimeException( th );
		}
	}

}
