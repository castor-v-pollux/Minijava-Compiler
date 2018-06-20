package k2m;

import java.io.InputStream;

import parser.KangaParser;
import parser.ParseException;
import syntaxtree.Goal;
import visitor.TranslateVisitor;

/**
 * <p>����Ϊ�������ҵ�����࣬����Kanga���뷭���MIPS���롣
 * <p>˼·Ϊ����Kanga��MIPS����伸����һһ��Ӧ�ģ���Ҫ�����Ƕ�ջ�Ĺ���
 * <p>������ֻʹ����һ��{@link TranslateVisitor}�������롣
 * <p>{@link kanga.Method}��������һ�����̵�ջ֡��ơ�
 * 
 * <p>��������Խ�ucla�����ϵ���������ɼ�����ȫ��ͬ�����������������ӵ�_error�������⣬Ψһ����������ջ��Ԫ�õı�����XD
 *
 * @author castor_v_pollux
 */
public class Main {

	public static void main(String[] args) {
		InputStream in = System.in;
		Goal goal;
		try {
			goal = new KangaParser(in).Goal();
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		goal.accept(new TranslateVisitor(), null);
	}

}
