
package cafe.woden.change;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.random.HaltonSequenceGenerator;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Temperature;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

public class Util
{

	public static BigInteger getRandomNumber()
	{
		BigInteger bigInteger = null;
		final StringBuffer randomNumber;
		try
		{
			SystemInfo si = new SystemInfo();
			HardwareAbstractionLayer hal = si.getHardware();
			//CentralProcessor cpu = hal.getProcessor();
			String procId = hal.getProcessor()
				.getProcessorIdentifier()
				.toString();
			randomNumber = new StringBuffer();
			procId.chars()
				.forEach( c -> randomNumber.append( (int) c ) );
			randomNumber.reverse();
			int loadAverage = (int) si.getHardware()
				.getProcessor()
				.getSystemLoadAverage( 1 )[0];
			if ( loadAverage > -1 )
			{
				randomNumber.append( String.valueOf( loadAverage ) );
			}
			for ( Temperature temp : getCpus() )
			{
				int tempValue = temp.value.intValue();
				randomNumber.append( String.valueOf( tempValue ) );
			}
			List<Integer> time = new ArrayList<>();
			String.valueOf( System.currentTimeMillis() )
				.chars()
				.forEach( time::add );
			randomNumber.append( String.valueOf( (hal.getMemory()
				.getTotal()
				% hal.getMemory()
					.getAvailable()) ) );
			Collections.shuffle( time );
			randomNumber.append( String.valueOf( System.currentTimeMillis() ) );
			List<Integer> numbers = new ArrayList<>();
			randomNumber.chars()
				.forEach( numbers::add );
			numbers.add( Math.abs( SecureRandom.getInstanceStrong()
				.nextInt() ) );

			Collections.reverse( numbers );
			Collections.shuffle( numbers );

			StringBuffer randomNumberFinal = new StringBuffer();
			for ( Integer integer : numbers )
			{
				randomNumberFinal.append( String.valueOf( integer ) );
			}
			bigInteger = new BigInteger( randomNumberFinal.toString() );
			return bigInteger;
		}
		catch ( Throwable th )
		{
			throw new RuntimeException( th );
		}
	}

	public static Set<Temperature> getCpus()
	{
		Components components = JSensors.get.components();

		Set<Temperature> allTemps = new HashSet<>();
		List<Cpu> cpus = components.cpus;
		if ( cpus != null )
		{
			for ( final Cpu cpu : cpus )
			{
				if ( cpu.sensors != null )
				{

					List<Temperature> temps = cpu.sensors.temperatures;

					allTemps.addAll( temps );
				}
			}

		}
		return allTemps;
	}

}
