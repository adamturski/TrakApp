package pl.com.turski.app.task;

import android.os.AsyncTask;
import android.util.Log;
import com.appspot.trak.shipment.Shipment;
import com.appspot.trak.shipment.model.ShipmentCreate;
import com.appspot.trak.shipment.model.ShipmentRegisterReturnDto;
import pl.com.turski.app.App;

import java.io.IOException;

/**
 * User: Adam
 */
public class CreateShipmentTask extends AsyncTask<ShipmentCreate, Void, Long> {

    @Override
    protected Long doInBackground(ShipmentCreate... shipmentCreates) {
        try {
            Shipment shipmentService = App.getShipmentService();
            ShipmentCreate shipmentCreate = shipmentCreates[0];
            ShipmentRegisterReturnDto shipmentRegisterReturnDto = shipmentService.create(shipmentCreate).execute();
            return shipmentRegisterReturnDto.getShipmentId();
        } catch (IOException e) {
            Log.e("TRAK_GPS", "IOException occured during creating shipment", e);
            return null;
        }
    }
}
