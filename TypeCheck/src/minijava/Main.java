package minijava;

import java.io.InputStream;

import parser.MiniJavaParser;
import parser.ParseException;
import symbol.MClass;
import symbol.MIdentifier;
import symbol.MMethod;
import symbol.MType;
import symbol.MVar;
import symbol.SymbolTable;
import syntaxtree.Identifier;
import syntaxtree.Node;
import visitor.ClassVisitor;
import visitor.MemberVisitor;
import visitor.TypeCheckVisitor;

/**
 * <p>����Ϊ��һ����ҵTypeCheck�����ࡣ
 * <p>���¶���������ļܹ����������̺����������ʹ������˵����
 * 
 * <p>�������MiniJava�ķ���ϵͳ�����ˣ�ʹ���ͼ����ԣ��ǳ����ŵķ�װ����֯����MiniJava�еı�ʶ������Ϊ�����ࣺ
 * <ul>
 * <li>{@link MIdentifier}    ��ʶ���ĸ��࣬��¼���ơ���������
 * <li>|---{@link MClass}     ��ʾһ���࣬ά���䷽���������б��
 * <li>|---{@link MMethod}    ��ʾһ��������ά��������������б��
 * <li>|---{@link MVar}       ��ʾһ��������ά��������
 * </ul>
 * 
 * <p>���⽫MiniJava�е����ͳ���Ϊ���½ӿڣ�
 * <ul>
 * <li>{@link MType}                 ���͵ĸ��ӿڣ���������{@link MType#getName()}��{@link MType#canConvertTo(MType)}
 * <li>|---{@link MClass}            ����Ϊ�������͵ĳ����ߣ��̳иýӿ�
 * <li>|---{@link MType#IntType}     �ӿڵĳ���ʵ�֣���ʾ{@code int}����
 * <li>|---{@link MType#BooleanType} �ӿڵĳ���ʵ�֣���ʾ{@code boolean}����
 * <li>|---{@link MType#ArrayType}   �ӿڵĳ���ʵ�֣���ʾ{@code int[]}����
 * <li>|---{@link MType#OtherType}   �ӿڵĳ���ʵ�֣���ʾ{@code String[]}����
 * </ul>
 * <p>�������ĳ������װ�£����ǿ��Էǳ����ɵؽ�����MiniJava�ķ��ű�����������ͼ�飬������Ҫ�ڸ߲���ַ��������κβ�����������{@link SymbolTable}��Ϊ�����࣬����ά���������ű�
 * 
 * <p>���������ͼ����Ҫ��Ϊ�Ĳ���ʹ��������Visitor���б������������£�
 * <ul>
 * <li>1.ʹ��{@link ClassVisitor}��������������������֣�����Щ�������ű���
 * <li>2.ʹ��{@link MemberVisitor}������������ĳ�Ա�����ͷ������Լ����������ľֲ������������Ǽ��뵽���ű���
 * <li>3.ʹ��{@link SymbolTable#checkClassExtension()}���ڷ��ű��ڲ������ļ̳й�ϵ����ʹ����������ȷ���̳й�ϵ�޻��󣬸�������˳��������Ա�����ͷ����ļ̳�
 * <li>4.ʹ��{@link TypeCheckVisitor}���������������е�����б����ý��õķ��ű�����������ͱ��ʽ�����ͼ��
 * </ul>
 * <p>Ϊʲô�������ű�ʹ������Visitor������һ����?��Ȼ��һ��Visitorֻ�����������˱������Ҽ�����ű��У����Ᵽ֤�˵ڶ��α���ʱ�������õ����඼�Ѿ��ڷ��ű��У������������������ͣ��̳еĸ���ȣ�����ֱ�����õ���Ӧ�����ϡ�
 * <p>��������α����ϲ�����ô������һ������ʱ��������Ϳ����ں���Ż�������ʹ�����ǲ��ò����ַ����������δ�������࣬�ڽ����ű�����ɺ���ʹ����Щ�ַ���ȥ������͡��̳еȣ�����˳��������ԣ������ǶԷ��ű��������������㣡�
 * <p>��������ƣ���������Visitor�ж��ַ�������ʽ����ֻ�����ڶ�{@link Identifier}�ڵ�����У��߲�����ͼ��ȫ������{@link MIdentifier}������{@link MType}���У�ʵ���˸߶ȵķ�װ����
 * 
 * <p>�����������Ҫ����������ͼ��ȫ����ɣ�����ֱ��ɢ���ĸ������У�����ÿ�ִ���ļ������һ���н��У��ɲο������ĵ���
 * <ul>
 * <li>{@link ClassVisitor}
 * <li>{@link MemberVisitor}
 * <li>{@link SymbolTable#checkClassExtension()}
 * <li>{@link TypeCheckVisitor}
 * </ul>
 * 
 * <p>������δ��������Խ��ͱ�����ʼ���ļ�飬��Ϊ���������漰���Գ���������ͱ��ʽȡֵ�ȵľ�̬������ʮ�ָ����һ�Ӱ�쵱ǰ��������ͼ�����ŵ�ʵ��:(
 * 
 * @author castor_v_pollux
 */
public class Main {

	public static void main(String[] args) {
		InputStream in = System.in;
		// try {
		// in = new FileInputStream(args[0]);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// return;
		// }
		Node goal;
		try {
			goal = new MiniJavaParser(in).Goal();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			return;
		}
		goal.accept(new ClassVisitor());
		goal.accept(new MemberVisitor(), null);
		SymbolTable.checkClassExtension();
		goal.accept(new TypeCheckVisitor(), null);
		System.out.println("Program type checked successfully");
	}

}