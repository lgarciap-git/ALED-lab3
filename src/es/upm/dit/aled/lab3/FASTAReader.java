package es.upm.dit.aled.lab3;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Lee un archivo FASTA que contiene información genética y permite la búsqueda
 * de patrones específicos dentro de estos datos. La información se almacena como un
 * array de bytes que contiene nucleótidos en formato FASTA. Dado que este array
 * normalmente se crea antes de saber cuántos caracteres del archivo FASTA original
 * son válidos, también se almacena un entero que indica cuántos bytes del array son válidos.
 * Todos los caracteres válidos estarán al comienzo del array.
 * 
 * @author mmiguel, rgarciacarmona
 *
 */
public class FASTAReader {

	protected byte[] content; // Este será nuestro genoma
	protected int validBytes; // solo serán válidos los elementos desde content[0] hasta content[validBytes- 1]

	/**
	 * Creates a new FASTAReader from a FASTA file.
	 * 
	 * @param fileName The name of the FASTA file.
	 */
	public FASTAReader(String fileName) {
		try {
			this.readFile(fileName);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return;
		}
	}

	/*
	 * Método auxiliar para leer desde un archivo. Llena el array de datos con la
	 * versión en mayúsculas de todos los nucleótidos encontrados en el archivo.
	 * Lanza una IOException si hay un problema al acceder al archivo o si el archivo
	 * es demasiado grande para caber en un array.
	 */
	private void readFile(String fileName) throws IOException {
		File f = new File(fileName);
		FileInputStream fis = new FileInputStream(f);
		DataInput fid = new DataInputStream(fis);
		//Cast  para guardarlo en un int
		long len = (int) fis.getChannel().size(); //fis.getChnn().size() devuelve el tamaño del archivo en bytes, y su tipo es long, 
		if (len > Integer.MAX_VALUE) {		     //porque los archivos pueden ser muy grandes (mucho más que Integer.MAX_VALUE). 
			fis.close();
			throw new IOException("The file " + fileName + " is too big. Can't be contained in an array.");
		}
		byte[] content = new byte[(int) len]; //Los arrays en Java solo pueden tener tamaño int: cast seguro pq ya sé que len cabe en un int
		int bytesRead = 0;
		int numRead = 0; //Número de bytes que tiene la línea actual (line.length() abajo)
		String line; //Cada linea del fichero
		while ((line = fid.readLine()) != null) {
			// Put every character in upper case
			line = line.toUpperCase();
			numRead = line.length();
			byte[] newData = line.getBytes(); //Es un array temporal que contiene los bytes de una línea del archivo (line.getBytes()
			for (int i = 0; i < numRead; i++) //Bucle que recorre las lineas del fichero
				content[bytesRead + i] = newData[i]; //bytesRead + i (sabiendo que bR se actualiza en cada for) es para seguir llenando "content" 
				bytesRead += numRead; //A los bytes leidos se les añade los de la linea que ha recorrido el for
		}
		fis.close();
		this.content = content;
		this.validBytes = bytesRead;
	}

	/**
	 * Provides the data array that contains the complete sequence of nucleotids
	 * extracted from the FASTA file.
	 * 
	 * @return The data array with each nucleotid taking one byte.
	 */
	public byte[] getContent() {
		return content;
	}

	/**
	 * Proporciona la cantidad de bytes válidos en el array de datos. Dado que este
	 * array se crea antes de conocer la cantidad de bytes en el archivo FASTA que
	 * contienen nucleótidos reales, se asume un escenario de peor caso. Por lo tanto,
	 * solo las posiciones entre content[0] y content[validBytes - 1] contienen datos
	 * genómicos reales.
	 * 
	 * @return El número de bytes válidos.
	 */
	public int getValidBytes() {
		return validBytes;
	}

	/**
	 * Devuelve la secuencia de nucleótidos del tamaño indicado encontrada en la
	 * posición proporcionada del array de datos. Si initialPos + size supera la
	 * cantidad de bytes válidos del array, devuelve null.
	 * 
	 * @param initialPos El primer carácter de la secuencia.
	 * @param size       La longitud de la secuencia.
	 * @return Una cadena (String) que representa la secuencia.
	 */
	public String getSequence(int initialPos, int size) { // desde la posicion que se marque coge un intervalo del tamaó size
		if (initialPos + size >= validBytes)
			return null;
		return new String(content, initialPos, size);
	}

	/*
	 * Método auxiliar que verifica si un patrón aparece en una posición específica
	 * del array de datos. Comprueba cada byte del patrón uno por uno. Si la longitud
	 * del patrón requiere acceder a una posición después de los bytes válidos del
	 * array, lanza una nueva FASTAException para indicar este hecho.
	 * 
	 * Devuelve true si el patrón está presente en la posición indicada; false en caso contrario.
	 */

	private boolean compare(byte[] pattern, int position) throws FASTAException {
		if (position + pattern.length > validBytes) {
			throw new FASTAException("Pattern goes beyond the end of the file.");
		}
		boolean match = true;
		for (int i = 0; i < pattern.length; i++) {
			if (pattern[i] != content[position + i]) {
				match = false;
			}
		}
		return match;
	}

	/*
	 * Versión mejorada del método compare que deja de comprobar los elementos del
	 * patrón cuando se encuentra uno que es diferente.
	 */

	private boolean compareImproved(byte[] pattern, int position) throws FASTAException {
		// TODO
		return false;
	}

	/*
	 * Versión mejorada del método compare que devuelve el número de bytes en el
	 * patrón que son diferentes de los presentes en el array de datos en la posición
	 * indicada.
	 * 
	 * Devuelve el número de caracteres en el patrón que son distintos de los que
	 * están en la posició*
	 */
	private int compareNumErrors(byte[] pattern, int position) throws FASTAException {
		// TODO
		return -1;
	}

	/**
	 * Implementa una búsqueda lineal para encontrar el patrón proporcionado en el
	 * array de datos. Devuelve una lista de enteros que indican las posiciones
	 * iniciales de todas las apariciones del patrón en los datos.
	 * 
	 * @param pattern El patrón que se desea encontrar.
	 * @return Todas las posiciones del primer carácter de cada aparición del
	 *         patrón en los datos.
	 */

	public List<Integer> search(byte[] pattern) {
		List<Integer> listaPos = new ArrayList<Integer>();
		
		for(int i = 0; i <= (validBytes - pattern.length) ; i++) {
			
			boolean coincide = true;
			
			if(content[i] == pattern[0]) { 
				
				for(int j = 0 ; j < pattern.length ; j++) {
					if(content[i + j] != pattern[j]) { //empiezo desde i porque se que en esa posicion ha coincidido y a partri de ahi comparo cada posicion "j" y la j en content para ver si coinciden
						coincide = false;
						break;
					}
						
				
				}
			}
				
			if(coincide) {
				listaPos.add(i);
			}	
		}
		
		return listaPos;
	}
	
	/**
	 * Implementa una búsqueda lineal para encontrar el patrón proporcionado en el
	 * array de datos, pero permitiendo una SNV (Variación de Nucleótido Único).
	 * En una SNV, se permite que un nucleótido sea diferente al del patrón.
	 * Por lo tanto, este método devuelve una lista de enteros que indican las
	 * posiciones iniciales de todas las apariciones del patrón en los datos y
	 * de todas las apariciones del patrón con un error en los datos.
	 * 
	 * @param pattern El patrón que se desea encontrar.
	 * @return Todas las posiciones del primer carácter de cada aparición del
	 *         patrón (con hasta 1 error) en los datos.
	 */

	public List<Integer> searchSNV(byte[] pattern) {
		// TODO
		return null;
	}

	public static void main(String[] args) {
		long t1 = System.nanoTime();
		FASTAReader reader = new FASTAReader(args[0]);
		if (args.length == 1)
			return;
		System.out.println("Tiempo de apertura de fichero: " + (System.nanoTime() - t1));
		long t2 = System.nanoTime();
		List<Integer> posiciones = reader.search(args[1].getBytes());
		System.out.println("Tiempo de búsqueda: " + (System.nanoTime() - t2));
		if (posiciones.size() > 0) {
			for (Integer pos : posiciones)
				System.out.println("Encontrado " + args[1] + " en " + pos);
		} else
			System.out.println("No he encontrado : " + args[1] + " en ningun sitio");
		System.out.println("Tiempo total: " + (System.nanoTime() - t1));
	}
}
