package it.uniroma3.weir.vector.format;

public interface TypeDecoder {

	/**
	 * Decode a string representation of a typed object.
	 * @param string - a string to interpret according to this format
	 * @return the object interpretation of the input string
	 */
	public Object decode(String string);

	static public TypeDecoder IDENTITY = new TypeDecoder() {

		@Override
		public Object decode(String value) {
			return value;
		}
		
	};

}
