
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.formdev.flatlaf.FlatDarkLaf;
public class login_window {
    private JPanel rootJpanel;
    private JPanel mainJpanel;
    private JPanel loginJpanel;
    private JButton registerButton;
    private JTextField usernameText;
    private JPasswordField passwordText;
    private JButton loginButton;
    private JPanel background;


    //private ImageIcon backgroundIcon;
    public login_window() {
        // 为登录按钮添加点击事件监听器
        //backgroundIcon = new ImageIcon("F:\\edge_downloads\\login_image.jpg");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                real_login.main();//跳转到登录逻辑
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(rootJpanel);
                currentFrame.dispose();//关闭当前窗口
            }
        });



        // 为注册按钮添加点击事件监听器
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 这里是注册按钮的处理逻辑
                //JOptionPane.showMessageDialog(rootJpanel, "Navigating to Registration...");
                // 注册按钮点击后跳转到注册界面
                real_register.main();  // 假设 RegisterWindow 是你注册的窗口
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(rootJpanel);
                currentFrame.dispose();

            }
        });


    }



    public static void main() {
        FlatDarkLaf.setup();
        JFrame frame = new JFrame("login_window");
        frame.setBounds(500, 500, 1500, 1500);
        frame.setContentPane(new login_window().rootJpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


}
