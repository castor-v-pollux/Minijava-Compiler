package symbol;

/**
 * <p>该接口抽象了MiniJava中的变量类型。MiniJava中引用类型是类，基本类型有{@code int},
 * {@code boolean}, {@code int[]}和{@code String[]}.
 * <p>因此，MType接口的实现类为{@link MClass}，且另外有四个实例常量分别表示每一种基本类型:
 * <ul>
 * <li>{@link MType#IntType}      MiniJava中的{@code int}类型
 * <li>{@link MType#BooleanType}  MiniJava中的{@code boolean}类型
 * <li>{@link MType#ArrayType}    MiniJava中的{@code int[]}类型
 * <li>{@link MType#OtherType}    MiniJava中的{@code String[]}类型
 * </ul>
 *
 * @author castor_v_pollux
 */
public interface MType {

	/**
	 * @return 类型名
	 */
	String getName();

	/**
	 * 能否被转换为类型{@code type}。
	 * 对于引用类型，需要判断类的继承关系；对于基本类型，必须相等才能转换。
	 * @param type 目标类型
	 */
	boolean canConvertTo(MType type);

	/**
	 * Minijava中的int类型
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
	 * Minijava中的boolean类型
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
	 * Minijava中的int[]类型
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
	 * Minijava中main的参数类型（不会被实际用到）
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
