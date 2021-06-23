
package cafe.woden.change;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public class Main
{
	private static int TOTAL_VALUE;
	static
	{
		try
		{
			SecureRandom random = SecureRandom.getInstanceStrong();
			random.setSeed( Util.getRandomNumber()
				.longValue() );
			TOTAL_VALUE = random.ints( 100, 200 )
				.findAny()
				.getAsInt();
		}
		catch ( NoSuchAlgorithmException e )
		{
			throw new RuntimeException( e );
		}
	}

	public static void main( String[] args )
	{
		Collection<Coin> tailsCoins = new ArrayList<>();
		Collection<Coin> allCoins = Coin.getTotalValue( TOTAL_VALUE );
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
	}

	private static Coin shuffleCoin( Coin coin )
	{
		try
		{
			int shuffleSize = SecureRandom.getInstanceStrong()
				.ints( SecureRandom.getInstanceStrong()
					.ints( 1, 100 )
					.findAny()
					.getAsInt(),
					SecureRandom.getInstanceStrong()
						.ints( 100, 1000 )
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
