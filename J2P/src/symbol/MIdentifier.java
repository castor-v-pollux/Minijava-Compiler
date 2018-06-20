package symbol;

/**
 * �����ʾһ��MiniJava�ı�ʶ������ʶ�����������ࣺ{@link MClass}, {@link MMethod}, {@link MVar}.
 *
 * @author castor_v_pollux
 */
public class MIdentifier {

	private String name;

	private MIdentifier() {
	}

	public MIdentifier(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
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
