
package controlador;

import java.awt.Dimension;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import modelo.Producto;
import modelo.RepositorioProducto;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import vista.Principal;

/**
 *
 * @author Pablo
 */
public class ControladorProducto implements ActionListener{
    
    RepositorioProducto repoProducto;
    Principal vista;
    DefaultTableModel defaultTableModel;
    
    private int codigo;
    private String nombre;
    private double precio;
    private int inventario;

    public ControladorProducto() {
        super();
    }

    public ControladorProducto(RepositorioProducto repoProducto, Principal vista) {
        super();
        this.repoProducto = repoProducto;
        this.vista = vista;
        vista.setVisible(true);
        agregarEventos();
        listarTabla();
    }
    
    
    private void agregarEventos(){
        vista.getBtnAgregar().addActionListener(this);
        vista.getBtnActualizar().addActionListener(this);
        vista.getBtnBorrar().addActionListener(this);
        vista.getBtnInforme().addActionListener(this);
        vista.getTblTabla().addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                llenarCampos(e);
            }
        });
    }
    
    public void listarTabla(){
        String[] titulos=new String[]{"id", "Nombre", "Precio", "Inventario"};
        defaultTableModel=new DefaultTableModel(titulos,0);
        List<Producto> listaProductos = (List<Producto>) repoProducto.findAll();
        
        for (Producto producto : listaProductos) {
            defaultTableModel.addRow(new Object[]{
                producto.getCodigo(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getInventario()
            });
        }
        vista.getTblTabla().setModel(defaultTableModel);
        vista.getTblTabla().setPreferredSize(new Dimension(
                350, 
                defaultTableModel.getRowCount()*16));
    }
    
    private void llenarCampos(MouseEvent e){
        JTable target = (JTable) e.getSource();
        vista.getTxtCodigo().setText(vista.getTblTabla().getModel().getValueAt(target.getSelectedRow(),0).toString());
        vista.getTxtNombre().setText(vista.getTblTabla().getModel().getValueAt(target.getSelectedRow(),1 ).toString());
        vista.getTxtPrecio().setText(vista.getTblTabla().getModel().getValueAt(target.getSelectedRow(),2 ).toString());
        vista.getTxtInventario().setText(vista.getTblTabla().getModel().getValueAt(target.getSelectedRow(),3 ).toString());
    }
    
    
    //Validar vacíos
    private boolean validarDatos(){
        if("".equals(vista.getTxtNombre().getText()) || "".equals(vista.getTxtPrecio().getText()) || "".equals(vista.getTxtInventario().getText())){
            JOptionPane.showMessageDialog(null,"Los campos no pueden ser vacios","Error",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    //Cargar los datos y validar si precio e inventario son numericos
    private boolean cargarDatos(){
        try {
            codigo=Integer.parseInt("".equals(vista.getTxtCodigo().getText())?"0" : vista.getTxtCodigo().getText());
            nombre=vista.getTxtNombre().getText();
            precio=Double.parseDouble(vista.getTxtPrecio().getText());
            inventario=Integer.parseInt(vista.getTxtInventario().getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private void limpiarCampos(){
        vista.getTxtCodigo().setText("");
        vista.getTxtNombre().setText("");
        vista.getTxtPrecio().setText("");
        vista.getTxtInventario().setText("");
    }
    
    
    
    private  void agregarProducto(){
        try {
            if (validarDatos()){
                if (cargarDatos()) {
                    Producto producto=new Producto(nombre, precio, inventario);
                    repoProducto.save(producto);
                    JOptionPane.showMessageDialog(null, "Producto Agregado con exito");
                    limpiarCampos();
                } else {
                    JOptionPane.showMessageDialog(null,"Los campos precio e inventario deben ser numéricos","Error",JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (DbActionExecutionException e) {
            JOptionPane.showMessageDialog(null, "el Producto ya Existe");
        }finally{
            listarTabla();
        }
    }
    
    private  void actualizarProducto(){
        try {
            if (validarDatos()){
                if (cargarDatos()) {
                    Producto producto=new Producto(codigo, nombre, precio, inventario);
                    repoProducto.save(producto);
                    JOptionPane.showMessageDialog(null, "Producto Actualizado con exito");
                    limpiarCampos();
                } else {
                    JOptionPane.showMessageDialog(null,"Los campos precio e inventario deben ser numéricos","Error",JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (DbActionExecutionException e) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error");
        }finally{
            listarTabla();
        }
    }
    
    private  void borrarProducto(){
        try {
            if (validarDatos()){
                if (cargarDatos()) {
                    Producto producto=new Producto(codigo, nombre, precio, inventario);
                    repoProducto.delete(producto);
                    JOptionPane.showMessageDialog(null, "Producto Borrado con exito");
                    limpiarCampos();
                } 
            }
        } catch (DbActionExecutionException e) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error");
        }finally{
            listarTabla();
        }
    }
    
    private void generarInforme(){
        String nombreMayor=precioMayor();
        String nombreMenor=precioMenor();
        String promedio=precioPromedio();
        String total=totalInventario();
        JOptionPane.showMessageDialog(null, nombreMayor+""+nombreMenor+""+promedio+""+total);
    }
    
    private String precioMayor(){
        String nombre="";
        double precioAux=0;
        List<Producto> listaProductos=(List<Producto>) repoProducto.findAll();
        for (Producto producto : listaProductos) {
            if(producto.getPrecio()>precioAux){
                nombre=producto.getNombre();
                precioAux=producto.getPrecio();
                
            }
        }
        return nombre;
        
    }
    
    private String precioMenor(){
        String nombre="";
        double precioAux=1000000;
        List<Producto> listaProductos=(List<Producto>) repoProducto.findAll();
        for (Producto producto : listaProductos) {
            if(producto.getPrecio()<precioAux){
                nombre=producto.getNombre();
                precioAux=producto.getPrecio();
                
            }
        }
        return nombre;
        
    }
    
    private String precioPromedio(){
        double suma=0;
        double resultado=0;
        List<Producto> listaProductos=(List<Producto>) repoProducto.findAll();
        for (Producto producto : listaProductos) {
            suma+=producto.getPrecio();
        }
        resultado=suma/listaProductos.size();
        return String.format("%.2f", resultado);
    }
    
    private String totalInventario(){
        double suma=0;
        double resultado=0;
        List<Producto> listaProductos=(List<Producto>) repoProducto.findAll();
        for (Producto producto : listaProductos) {
            suma=producto.getPrecio()*producto.getInventario();
            resultado=resultado+suma;
        }
        
        return String.format("%.2f", resultado);
    
        
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource()==vista.getBtnAgregar()){
            agregarProducto();
        }
        if(ae.getSource()==vista.getBtnActualizar()){
            actualizarProducto();
        }
        if(ae.getSource()==vista.getBtnBorrar()){
            borrarProducto();
        }
        if(ae.getSource()==vista.getBtnInforme()){
            generarInforme();
        }
    }

    
    
    
}
