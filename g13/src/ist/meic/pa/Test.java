package ist.meic.pa;

public class Test {
	private int age;
	
	@KeywordArgs("age=10")
	public Test(){
		
	}
	
	@Override
	public String toString(){
		return "Coco age: " + getAge();
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
}
