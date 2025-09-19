package GUI;

import javax.swing.JFrame;

public class Marco extends JFrame{
    
public Marco(){

    setSize(900,900);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setVisible(true);


    Panel p= new Panel();
    add(p);
    revalidate();

}



}
