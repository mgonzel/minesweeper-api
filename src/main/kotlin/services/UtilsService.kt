package services;

import java.security.SecureRandom
import java.util.Base64
import java.util.Scanner

val DEFAULT_RANDOM_NAME_LENGTH = 8;

public class UtilsService { 
	val strChain : String = "lgwjzc7bxf9rs6dha2i8ven3t45q1kuypom0";

	fun getRandomName() : String {
        return getRandomName(DEFAULT_RANDOM_NAME_LENGTH)
    }
	fun getRandomName(length : Int ) : String{
		val sr = SecureRandom()
		val sb = StringBuffer()
		
		val maxIndex = strChain.length
		
		
		for (i in 1..length){
			sb.append(strChain.get(sr.nextInt(maxIndex)))
		}
		
		return sb.toString()
	}

    fun getRandomInt(max: Int) : Int {
        val sr = SecureRandom()

        return sr.nextInt(max) + 1
    }

}