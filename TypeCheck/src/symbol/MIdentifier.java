package symbol;

/**
 * �����ʾһ��MiniJava�ı�ʶ������ʶ�����������ࣺ{@link MClass}, {@link MMethod}, {@link MVar}.
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
	 * ������ʶ����ͬ���ҽ������ǵ�������ͬ��
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
	 * ��Ǳ�������Visitor�д���Identifier�۲��ߣ�����ָ����IdentifierΪһ���ࡣ
	 */
	public static final MIdentifier TAG_CLASS = new MIdentifier();

}
