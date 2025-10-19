package es.upm.dit.aled.lab3.binary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import es.upm.dit.aled.lab3.FASTAReader;

/**
 * Reads a FASTA file containing genetic information and allows for the search
 * of specific patterns within these data. The information is stored as an array
 * of bytes that contain nucleotides in the FASTA format. Since this array is
 * usually created before knowing how many characters in the origin FASTA file
 * are valid, an int indicating how many bytes of the array are valid is also
 * stored. All valid characters will be at the beginning of the array.
 * 
 * This extension of the FASTAReader uses a sorted dictionary of suffixes to
 * allow for the implementation of binary search.
 * 
 * @author mmiguel, rgarciacarmona
 *
 */
public class FASTAReaderSuffixes extends FASTAReader {
	protected Suffix[] suffixes;  //Contiene suffixes, un array de Suffix que lista las posiciones de los posibles sufijos del
	 							  //genoma sobre el que se busca.

	/**
	 * Creates a new FASTAReader from a FASTA file.
	 * 
	 * At the end of the constructor, the data is sorted through an array of
	 * suffixes.
	 * 
	 * @param fileName The name of the FASTA file.
	 */
	public FASTAReaderSuffixes(String fileName) {
		// LLama al constructor de la clase madre (FASTAReader) --> se hace con fileName lo que se hace en esa clase
		// que es:  -> Creates a new FASTAReader from a FASTA file
		super(fileName);
		this.suffixes = new Suffix[validBytes]; //array del tamaño de los bytes validos
		for (int i = 0; i < validBytes; i++) //se llena el array, ordenado "segun tamaño"
			suffixes[i] = new Suffix(i); 	//en la posicion 0 --> sufijo ; en la 1 --> sufijo sin la primera base etc
		// Sorts the data
		sort(); //aqui lo ordena alfabeticamente
	}

	/*
	 * Helper method that creates a array of integers that contains the positions of
	 * all suffixes, sorted alphabetically by the suffix.
	 */
	private void sort() {
		// Instantiate the external SuffixComparator, passing 'this' (the reader)
		// so it can access the content and validBytes fields.
		SuffixComparator suffixComparator = new SuffixComparator(this);
		// Use the external Comparator for sorting.
		Arrays.sort(this.suffixes, suffixComparator);
	}

	/**
	 * Prints a list of all the suffixes and their position in the data array.
	 */
	public void printSuffixes() {
		System.out.println("-------------------------------------------------------------------------");
		System.out.println("Index | Sequence");
		System.out.println("-------------------------------------------------------------------------");
		for (int i = 0; i < suffixes.length; i++) {
			int index = suffixes[i].suffixIndex;
			String ith = "\"" + new String(content, index, Math.min(50, validBytes - index)) + "\"";
			System.out.printf("  %3d | %s\n", index, ith);
		}
		System.out.println("-------------------------------------------------------------------------");
	}

	/**
	 * Implementa una búsqueda binaria para buscar el patrón proporcionado en el array de datos.
	 * Devuelve una lista de enteros que indican las posiciones iniciales de todas las
	 * ocurrencias del patrón en los datos.
	 * 
	 * @param pattern El patrón que se desea encontrar.
	 * @return Todas las posiciones del primer carácter de cada ocurrencia del
	 *         patrón en los datos.
	 */
	
	//Para saber los indices dentro de ocntent en los que está el patrón voy a buscarlo en sufixxes --> un array
	//ordenado alfabeticamente que me proporciona el indice en el que está su ".." dentro de content 
	//Es más facil buscar en suffixes --> complejidad nivel logaritmo ; ya está ordenado
	@Override
	public List<Integer> search(byte[] pattern) {
		List<Integer> listaP = new ArrayList<Integer>();
		int hi = suffixes.length -1 ; //empieza siendo la ultima posicion  (incluida)
		int lo = 0;					//empieza siendo 0
		
		int mid = 0;
		int posSuf = 0;
		int index = 0; //lo que miro en patter (posicion)
		boolean encontrado = false;
		
		do {
			mid = (int) Math.floor(lo + (hi-lo)/2); //se actualiza cada vez 
			posSuf = suffixes[mid].suffixIndex;
			
			//Recorro pattern con el indice que sera la posicion dentro de pattern, compararé con suffixes desde el medio 
			for(index = 0; index < pattern.length; index++) {
				//Que se rompa el for si hay diferencia
				if(pattern[index] != content[posSuf + index]) 	//sale del for cuando index = pattern.length 
					break;
			}
			//Si se da qeu index llega a ser la ultima posc del patrón es porque el bucle se ha recorrido del todo y
			//por lo tanto todos los caracteres coinciden --> encontrado = true
			if(index == pattern.length) {
				encontrado = true;
				listaP.add(posSuf);
			}
			else {
				if(pattern[index] < content[posSuf + index]) 
					hi = mid -1;
				else 
					lo = mid + 1;
			}						
			
		} while (!encontrado && hi - lo > 1);
		
		return listaP;
	}

	
	
	public static void main(String[] args) {
		long t1 = System.nanoTime();
		FASTAReaderSuffixes reader = new FASTAReaderSuffixes(args[0]);
		if (args.length == 1)
			return;
		byte[] patron = args[1].getBytes();
		System.out.println("Tiempo de apertura de fichero: " + (System.nanoTime() - t1));
		long t2 = System.nanoTime();
		System.out.println("Tiempo de ordenación: " + (System.nanoTime() - t2));
		reader.printSuffixes();
		long t3 = System.nanoTime();
		List<Integer> posiciones = reader.search(patron);
		System.out.println("Tiempo de búsqueda: " + (System.nanoTime() - t3));
		if (posiciones.size() > 0) {
			for (Integer pos : posiciones)
				System.out.println("Encontrado " + args[1] + " en " + pos);
		} else
			System.out.println("No he encontrado " + args[1] + " en ningún sitio.");
		System.out.println("Tiempo total: " + (System.nanoTime() - t1));
	}
}
