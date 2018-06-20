package symbol;

/**
 * 该类表示一个MiniJava的标识符。标识符有三个子类：{@link MClass}, {@link MMethod}, {@link MVar}.
 *
 * @author castor_v_pollux
 */
public class MIdentifier {

	private String name;

	private int row, column;

	private MIdentifier() {
	}

	public MIdentifier(String name, int row, int column) {
		this.name = name;
		this.row = row;
		this.column = column;
	}

	public String getName() {
		return name;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	/**
	 * 两个标识符相同当且仅当它们的名字相同。
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj != null && obj instanceof MIdentifier) {
			return ((MIdentifier) obj).name.equals(name);
		}
		return false;
	}

	/**
	 * 标记变量。在Visitor中传给Identifier观察者，用来指定该Identifier为一个类。
	 */
	public static final MIdentifier TAG_CLASS = new MIdentifier();

}
