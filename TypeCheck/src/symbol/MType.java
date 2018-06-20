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
	 * �ܷ�ת��Ϊ����{@code type}��
	 * �����������ͣ���Ҫ�ж���ļ̳й�ϵ�����ڻ������ͣ�������Ȳ���ת����
	 * @param type Ŀ������
	 */
	boolean canConvertTo(MType type);

	/**
	 * Minijava�е�int����
	 */
	MType IntType = new MType() {
		@Override
		public String getName() {
			return "int";
		}

		@Override
		public boolean canConvertTo(MType type) {
			return type == this;
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

		@Override
		public boolean canConvertTo(MType type) {
			return type == this;
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

		@Override
		public boolean canConvertTo(MType type) {
			return type == this;
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

		@Override
		public boolean canConvertTo(MType type) {
			return type == this;
		}
	};

}
