package es.upm.dit.aled.lab3.binary;

import java.util.Comparator;

/**
 * A custom Comparator for sorting Suffix objects based on the content of the
 * FASTA file. It needs access to the FASTA file's data array.
 *
 * @author mmiguel, rgarciacarmona
 */
public class SuffixComparator implements Comparator<Suffix> {

	// Since FASTAReader is the base class containing content and validBytes,
	// we need a reference to it to access the data.
	private final FASTAReaderSuffixes reader;

	/**
	 * Constructs a SuffixComparator, initializing it with the data provider.
	 * 
	 * @param reader The FASTAReader object that holds the FASTA sequence data.
	 */
	public SuffixComparator(FASTAReaderSuffixes reader) {
		this.reader = reader;
	}

	/**
	 * Compares two Suffix objects by comparing the byte sequences starting at their
	 * respective indices in the FASTA data array.
	 * 
	 * @param s1 The first Suffix.
	 * @param s2 The second Suffix.
	 * @return -1, 0, or 1 as the first suffix is less than, equal to, or greater
	 *         than the second.
	 */
	
	//Suffix --> Un objeto que representa un sufijo del texto = el "patrón a buscar"
	//SuffixIndex --> La posición donde empieza ese sufijo en el texto = si es el patrón enterop.ej su suffixIndex es 0
	//																	si se le quita la primera letra, es 1
	@Override
	public int compare(Suffix s1, Suffix s2) {
		int index1 = s1.suffixIndex; //la posición de los sufijos que quiero comparar
		int index2 = s2.suffixIndex;

		//Esto calcula cuántos bytes puedo comparar sin salirme del array, partiendo desde cada uno de los sufijos.
		//El mínimo asegura que si uno de los sufijos es más corto (por estar más cerca del final), no intento acceder fuera de los límites.
		
		//Eso te da el número máximo de caracteres comparables entre los dos sufijos sin salirte del array
		//Por ejemplo si quiero comparar dentro de un patrón de longitud 5, unos contenidos que empeizan en el
		//indice 2 y 3 del "patrón" va a haber un momento en el que no se puedan seguir comparando porque
		//la longitud del que empiea en el 3 es 5-3 = 2 y la del otro es 5-2 = 3 entonces para evitar ese caso
		//se hace esto del min --> obtiene el máx numero de caracteres comparables entre los 2 sufijos sin salirte del array
		int len = Math.min(this.reader.getValidBytes() - index1, this.reader.getValidBytes() - index2);

		for (int i = 0; i < len; i++) {
			if (this.reader.getContent()[index1 + i] < this.reader.getContent()[index2 + i])
				return -1; //en el momento en el que se cumple lo del if, ya sabe que el sufijo de index2 va antes que del index1
							//entonces no necesita seguir comparando 
			if (this.reader.getContent()[index1 + i] > this.reader.getContent()[index2 + i])
				return 1;
		}
		
		/*Solo se ejecuta si se recorren todos y no hay diferencia --> decide según la longitud
	      	      
	      P.ej, si comparo dentro de A A A, el sufijo s1 de indice 0 = AAA, el sufijo s2 de indice 1 = AA
	      entonces aqui se va a comparar dentor de for hasta la longitud del mas corto (por len), dandose que 
	      se tien el mismo contenido entonces no se cumple ni el < con return -1 ni el > con return 1 
	      Sale del for y llega al return index2 - index1 = 1 - 0 --> esto signifcia que el indice 1 va antes que el indice 2: "AAA" > "AA"
		*/
		return index2 - index1;
	}
}