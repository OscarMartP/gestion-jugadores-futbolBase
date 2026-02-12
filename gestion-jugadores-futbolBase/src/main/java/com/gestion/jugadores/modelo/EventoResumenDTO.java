package com.gestion.jugadores.modelo;

public class EventoResumenDTO {
	private String tipoEvento;
    private Integer minuto;
    private Long partidoId;
    private String fechaPartido;
	public String getTipoEvento() {
		return tipoEvento;
	}
	public void setTipoEvento(String tipoEvento) {
		this.tipoEvento = tipoEvento;
	}
	public Integer getMinuto() {
		return minuto;
	}
	public void setMinuto(Integer minuto) {
		this.minuto = minuto;
	}
	public Long getPartidoId() {
		return partidoId;
	}
	public void setPartidoId(Long partidoId) {
		this.partidoId = partidoId;
	}
	public String getFechaPartido() {
		return fechaPartido;
	}
	public void setFechaPartido(String fechaPartido) {
		this.fechaPartido = fechaPartido;
	}
    
    

}
