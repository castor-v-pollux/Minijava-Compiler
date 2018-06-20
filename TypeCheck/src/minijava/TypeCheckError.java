package minijava;

/**
 * 该类封装一个类型检查错误。
 *
 * @author castor_v_pollux
 */
public class TypeCheckError extends Error {

	/**
	 * 错误出现的行和列
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
