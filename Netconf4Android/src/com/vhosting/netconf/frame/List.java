package com.vhosting.netconf.frame;

import com.vhosting.netconf.transport.Capability;

/**
 * This class represents a list in the PRC structure.
 * A list is a special element that can be linked
 * to any other element to create complex structures.
 * 
 * A list is the most complex structure within the PRC structure.
 * 
 * It's very similar to a container with not presence,
 * but allows the assignment of their sub elements to a
 * minimum to a maximum of times.
 * 
 * Can be connected to a list elements such as:<br>
 * <br>
 * 
 * <pre>
 * Leaf, LeafList, Anyxml, ContainerReference, ListReference.
 * </pre>
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 09/10/2010
 */
public class List extends ListReference implements Node, Clearable
{

	private ListValue[] values;

	/**
	 * Create a list.
	 * 
	 * @param cap
	 *            The capability that this list reference belongs.
	 * @param name
	 *            The name of the list.
	 * @param min
	 *            The minimum limit of set of assignments which can be assigned.
	 * @param max
	 *            The maximum limit of set of assignments which can be assigned.
	 */
	public List(Capability cap, String name, long min, long max)
	{
		super(cap, name, min, max);

	}

	/**
	 * Create a list.
	 * 
	 * @param cap
	 *            The capability that this list reference belongs.
	 * @param name
	 *            The name of the list.
	 * @param min
	 *            The minimum limit of set of assignments which can be assigned.
	 */
	public List(Capability cap, String name, long min)
	{
		super(cap, name, min);

	}

	/**
	 * Create a list.
	 * 
	 * @param cap
	 *            The capability that this list reference belongs.
	 * @param name
	 *            The name of the list.
	 */
	public List(Capability cap, String name)
	{
		super(cap, name);
	}

	@Override
	public void clear() {

		if (values != null)
			for (ListValue v : values)
			{
				v.clear();
			}
	}

	@Override
	public boolean hasValues() {
		boolean hasValues = false;
		if (values != null)
		{
			for (ListValue lv : values)
			{
				hasValues = hasValues || lv.hasValues();
			}
		}
		return hasValues;
	}

	/**
	 * Create an instance of ListValue compatible with this list.
	 * 
	 * A ListValue instance is an assignment of the whole structure of a list.
	 * 
	 * @return An empty ListValue instance compatible with this list.
	 */
	public ListValue createListValue() {
		return new ListValue(this);
	}

	/*
	 * Note: This method is depreceted.
	 * Ma ancora deve essere scritta l'alternativa.
	 * 
	 * Alternativa: Ci deve essere una Vector che prende i valori
	 * e ogni volta che viene compiuta una azione di inserimento, modifica e
	 * rimozione
	 * fare i controlli.
	 * 
	 * Per la modifica è un macello e quindi deve essere fatto un metodo
	 * separato che fa il check per essere chiamato anche in modifica.
	 * 
	 */
	
	/**
	 * Assign the set of values for this list.
	 * @param lv An array of ListValue instances. 
	 */
	public void assign(ListValue[] lv) {
		if (!(min <= lv.length && lv.length <= max))
			throw new RuntimeException("Invalid number of elements: (min="
					+ min + "; max=" + max + ") Array elements: " + lv.length);

		doIntegrityCheck(key, unique, lv);

		// Inserisce i dati
		values = lv;

	}

	static void doIntegrityCheck(Leaf[] key, Leaf[][] unique, ListValue[] lv) {
		// Controlla l'esistenza delle chiavi.
		int i = 0;
		for (ListValue v : lv)
		{
			if (key != null)
				for (Leaf k : key)
				{
					if (v.value.get(k.getUniqueNane()) == null)
						throw new RuntimeException(
								"Key elements not exist at the array reference: "
										+ i);
				}
			i++;
		}

		// Controlla la univocità degli elementi.
		if (unique != null)
			for (Leaf[] uu : unique)
			{
				String[] g = new String[uu.length];
				int n = 0;
				for (ListValue v : lv)
				{
					String gen = new String("");
					for (Leaf u : uu)
					{
						String s = (String) v.value.get(u.getUniqueNane());
						if (s != null)
							gen += s;
					}
					g[n] = gen;
					n++;
				}
				boolean b = true;

				for (int x = 0; x < g.length; x++)
				{
					for (int y = 0; y < g.length; y++)
					{
						if (g[x].equals(g[y]))
						{
							b = false;
							break;
						}
					}
					if (!b)
						break;
				}
				if (!b)
				{
					throw new RuntimeException(
							"The data do not meet the rules specified for uniqueness.");
				}
			}
	}

	/**
	 * Get all instances of ListValue associated with the list.
	 * They represent the different assignments for the same list.
	 * 
	 * All together they represent the assignments of the list.
	 * 
	 * @return All instances of ListValue associated with the list.
	 */
	public ListValue[] getValues() {
		return values;
	}
}
