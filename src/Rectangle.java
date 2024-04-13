public class Rectangle implements IShape{
    private double length;
    private double width;
    public Rectangle(double length,double width){

        this.length=length;
        this.width=width;
    }


    public double getArea() {
        return this.length*this.width;
    }


    public double getPerimeter() {
        return (this.length+this.width)*2;
    }
}
