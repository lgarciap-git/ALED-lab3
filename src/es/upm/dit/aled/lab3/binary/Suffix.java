package es.upm.dit.aled.lab3.binary;

/**
 * Represents a suffix in the FASTA data by storing its starting index. This
 * class is designed to be sorted by an external Comparator that uses the actual
 * sequence data.
 *
 * @author mmiguel, rgarciacarmona
 */
public class Suffix {
	public final int suffixIndex; 		// Eentero que indica en qué posición de content se encuentra dicho sufijo

	/**
	 * Crea un nuevo sufijo.
	 * 
	 * @param index The starting position of the suffix in the data array.
	 */
	public Suffix(int index) {
		suffixIndex = index;
	}
}

//sufijo 0 --> patrón entero
//sufijo 1 --> se le quita la primera base al patrón

//cada sufijo solo guarda el entero donde empeiza el patrón en el array de content --> posición en content!!