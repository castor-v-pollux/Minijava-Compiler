package minijava;

/**
 * �����װһ�����ͼ�����
 *
 * @author castor_v_pollux
 */
public class TypeCheckError extends Error {

	/**
	 * ������ֵ��к���
	 */
	private int row, column;

	public TypeCheckError(String msg, int row, int column) {
		super(msg);
		this.row = row;
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

}
