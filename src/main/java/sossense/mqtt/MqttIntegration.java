package sossense.mqtt;

import java.beans.PropertyChangeListener;
import java.util.function.Supplier;

import sossense.datubasea.PanelPlano;

public final class MqttIntegration {

    private MqttIntegration() {}

    // Conecta el listener MQTT y actualiza el panel activo sin acoplar la UI
    public static void attachPlanUpdater(Mqtt mqtt,
                                         Supplier<PanelPlano> panelSupplier,
                                         Supplier<String> instalacionSupplier) {
        PropertyChangeListener listener = evt -> {
            if ("DATO_GAS_ACTUALIZADO".equals(evt.getPropertyName())) {
                double valorMedia = 0.0;
                Object nv = evt.getNewValue();
                if (nv instanceof Number) {
                    valorMedia = ((Number) nv).doubleValue();
                } else {
                    try { valorMedia = Double.parseDouble(String.valueOf(nv)); } catch (Exception ignored) {}
                }

                PanelPlano panel = panelSupplier.get();
                String instalacionActiva = instalacionSupplier.get();

                // Mantiene la misma lógica actual (solo Fabrika y sensor S1)
                if (panel != null && "Mondragon Fabrika".equalsIgnoreCase(instalacionActiva)) {
                    panel.actualizarSensorEspecifico("S1", (int) valorMedia);
                    System.out.println(">>> Mapa actualizado: S1 = " + valorMedia);
                }
            }
        };

        mqtt.addPropertyChangeListener(listener);
    }
}
