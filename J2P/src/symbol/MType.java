package symbol;

/**
 * <p>�ýӿڳ�����MiniJava�еı������͡�MiniJava�������������࣬����������{@code int},
 * {@code boolean}, {@code int[]}��{@code String[]}.
 * <p>��ˣ�MType�ӿڵ�ʵ����Ϊ{@link MClass}�����������ĸ�ʵ�������ֱ��ʾÿһ�ֻ�������:
 * <ul>
 * <li>{@link MType#IntType}      MiniJava�е�{@code int}����
 * <li>{@link MType#BooleanType}  MiniJava�е�{@code boolean}����
 * <li>{@link MType#ArrayType}    MiniJava�е�{@code int[]}����
 * <li>{@link MType#OtherType}    MiniJava�е�{@code String[]}����
 * </ul>
 *
 * @author castor_v_pollux
 */
public interface MType {

	/**
	 * @return ������
	 */
	String getName();

	/**
	 * Minijava�е�int����
	 */
	MType IntType = new MType() {
		@Override
		public String getName() {
			return "int";
		}
	};

	/**
	 * Minijava�е�boolean����
	 */
	MType BooleanType = new MType() {
		@Override
		public String getName() {
			return "boolean";
		}
	};

	/**
	 * Minijava�е�int[]����
	 */
	MType ArrayType = new MType() {
		@Override
		public String getName() {
			return "int[]";
		}
	};

	/**
	 * Minijava��main�Ĳ������ͣ����ᱻʵ���õ���
	 */
	MType OtherType = new MType() {
		@Override
		public String getName() {
			return "String[]";
		}
	};

}
