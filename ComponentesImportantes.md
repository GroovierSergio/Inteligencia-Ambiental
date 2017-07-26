# Componentes importantes
## - _Este documento se muestran componentes importantes de la app para su fácil comprensión

### BroadcastReceiver_BTState
Como esta app trata principalmente de señales de bluetooth, a continuación se describe una clase que su función es detectar cuando el adaptador bluetooth cambie de estado y notificar con un toast y asi saber que pasa en el dispositivo o teléfono inteligente.

```java

public class BroadcastReceiver_BTState extends BroadcastReceiver {
    Context activityContext;
    public BroadcastReceiver_BTState(Context activityContext) {
        this.activityContext = activityContext;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Utils.toast(activityContext, "Bluetooth is off");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Utils.toast(activityContext, "Bluetooth is turning off...");
                    break;
                case BluetoothAdapter.STATE_ON:
                    Utils.toast(activityContext, "Bluetooth is on");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                   Utils.toast(activityContext, "Bluetooth is turning on...");
                    break;
            }
        }
    }
}
```
