package com.gifa_api.model;

import com.gifa_api.utils.enums.EstadoDeHabilitacion;
import com.gifa_api.utils.enums.EstadoVehiculo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehiculo")
public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "patente", nullable = false, length = 10, unique = true)
    private String patente;

    @Column(name = "antiguedad")
    private Integer antiguedad;

    @Column(name = "kilometraje")
    private Integer kilometraje;

    @Column(name = "modelo", length = 50)
    private String modelo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_vehiculo", nullable = false)
    private EstadoVehiculo estadoVehiculo;

    @Enumerated(EnumType.STRING)
    @Column(name = "habilitado", nullable = false)
    private EstadoDeHabilitacion estadoDeHabilitacion;

    @Column(name = "qr")
    private byte[] qr;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;


    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL)
    private Set<KilometrajeVehiculo> kilometrajeVehiculos;

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL)
    private Set<Mantenimiento> mantenimientos;

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL)
    private Set<Chofer> chofers;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarjeta_id")
    private Tarjeta tarjeta;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "dispositivo_id")
    private Dispositivo dispositivo;

    public void inhabilitar(){
        this.estadoDeHabilitacion= EstadoDeHabilitacion.INHABILITADO;
    }
    public void habilitar(){
        this.estadoDeHabilitacion= EstadoDeHabilitacion.HABILITADO;
    }

}