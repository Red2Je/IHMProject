package model;

public class Main {

	public static void main(String[] args) {
		Model model = new Model();
		System.out.println(model.getData(1880, -88, -178));
		System.out.println(model.get().size());
		System.out.println((float)model.getMin());


	}

}
