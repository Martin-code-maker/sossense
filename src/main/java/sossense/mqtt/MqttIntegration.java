package sossense.mqtt;

import java.beans.PropertyChangeListener;
import java.util.function.Supplier;

import sossense.datubasea.PanelPlano;

public final class MqttIntegration {

    private MqttIntegration() {}

    // Conecta el listener MQTT y actualiza el panel activo sin acoplar la UI
    public static void attachPlanUpdater(Mqtt mqtt,
                                         Supplier<PanelPlano> panelSupplier,
                                         Supplier<String> instalacionSupplier,
                                         Supplier<String> planoSupplier,
                                         sossense.datubasea.AppContext appContext) {
        PropertyChangeListener listener = evt -> {
            if ("DATO_GAS_ACTUALIZADO".equals(evt.getPropertyName())) {
                Object nv = evt.getNewValue();
                
                // Parsear datos: [instalacion, plano, sensor, valor]
                if (nv instanceof String[]) {
                    String[] datos = (String[]) nv;
                    if (datos.length == 4) {
                        String instalacion = datos[0];
                        String plano = datos[1];
                        String sensor = datos[2];
                        double valorMedia = Double.parseDouble(datos[3]);
                        
                        // Convertir el valor según la especificación:
                        // Si < 400 -> 0
                        // Si >= 400 -> porcentaje (400=1%, 4096=100%)
                        int valorProcesado;
                        if (valorMedia < 400) {
                            valorProcesado = 0;
                        } else {
                            // Fórmula: ((valor - 400) / (4096 - 400)) * 99 + 1
                            double porcentaje = ((valorMedia - 400) / (4096 - 400)) * 99 + 1;
                            valorProcesado = (int) Math.round(porcentaje);
                            // Asegurar que no supere 100%
                            if (valorProcesado > 100) {
                                valorProcesado = 100;
                            }
                        }
                        
                        PanelPlano panel = panelSupplier.get();
                        String instalacionActiva = instalacionSupplier.get();
                        String planoActivo = planoSupplier.get();

                        // Guardar valor global para que otras vistas lo usen
                        if (appContext != null) {
                            appContext.setSensorValue(instalacion, plano, sensor, valorProcesado);
                        }
                        
                        // Actualizar solo si instalación y plano coinciden
                        if (panel != null
                                && instalacion.equalsIgnoreCase(instalacionActiva)
                                && plano.equalsIgnoreCase(planoActivo)) {
                            panel.actualizarSensorEspecifico(sensor, valorProcesado);
                            System.out.println(">>> Mapa actualizado: " + instalacion + "/" + plano + "/" + sensor + " | Valor crudo: " + valorMedia + " | Porcentaje: " + valorProcesado + "%");
                        }
                    }
                }
            }
        };

        mqtt.addPropertyChangeListener(listener);
    }
}
