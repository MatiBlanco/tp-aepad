package isi.aepad.tp.modelo;

import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@XmlRootElement
public class Factura {


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Temporal(TemporalType.DATE)
	private Date fecha;
	
	@ManyToOne
	@JoinColumn(name="ID_CLIENTE")
	private Usuario cliente;

	@OneToMany(mappedBy="factura")
	private List<FacturaDetalle> detalles;
	
	@XmlTransient
	@OneToMany(mappedBy="factura")
	private List<Pago> pagos;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Usuario getCliente() {
		return cliente;
	}
	public void setCliente(Usuario cliente) {
		this.cliente = cliente;
	}
	
	@XmlTransient
	public List<FacturaDetalle> getDetalles() {
		return detalles;
	}
	public void setDetalles(List<FacturaDetalle> detalles) {
		this.detalles = detalles;
	}
	@Override
	public String toString() {
		return "Factura [id=" + id + ", fecha=" + fecha + ", cliente=" + cliente + "]";
	}
	
	@XmlTransient
	public List<Pago> getPagos() {
		return pagos;
	}
	public void setPagos(List<Pago> pagos) {
		this.pagos = pagos;
	}
	
	
}
