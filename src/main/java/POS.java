import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class POS extends JFrame{
    JPanel mainPanel;
    private JTextField txtMaterial_ID;
    private JLabel MaterialIDLabel;
    private JTextField txtQuantity;
    private JTextField txtSellingPrice;
    private JTextField txtCustomer;
    private JTextField txtPaymentType;
    private JButton btn_enter;

    public POS(String title){

        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        btn_enter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)  {
                String MaterialID = txtMaterial_ID.getText();
                Double quantity = Double.parseDouble(txtQuantity.getText());
                Double price = Double.parseDouble(txtSellingPrice.getText());
                String customer = txtCustomer.getText();
                String paymentType = txtPaymentType.getText();
                SheetsAndJava sj = new SheetsAndJava();
                try {
                    sj.FindRow(MaterialID,quantity);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (GeneralSecurityException generalSecurityException) {
                    generalSecurityException.printStackTrace();
                }
                try {
                    sj.updateTransactions(MaterialID,quantity,price,customer,paymentType);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (GeneralSecurityException generalSecurityException) {
                    generalSecurityException.printStackTrace();
                }

            }
        });
    }
    public static void main(String[] args)  {
        JFrame frame = new POS("Texas POS");
        frame.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
