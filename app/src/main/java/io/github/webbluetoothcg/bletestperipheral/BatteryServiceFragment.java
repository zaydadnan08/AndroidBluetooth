/*
 * Copyright 2015 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.webbluetoothcg.bletestperipheral;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class BatteryServiceFragment extends ServiceFragment {

  private static final UUID BATTERY_SERVICE_UUID = UUID
      .fromString("0000180F-0000-1000-8000-00805f9b34fb");

  private static final UUID BATTERY_LEVEL_UUID = UUID
      .fromString("00002A19-0000-1000-8000-00805f9b34fb");
  private static final int INITIAL_BATTERY_LEVEL = 50;
  private static final int BATTERY_LEVEL_MAX = 100;
  private static final String BATTERY_LEVEL_DESCRIPTION = "The current charge level of a " +
      "battery. 100% represents fully charged while 0% represents fully discharged.";

//  String tmpstr = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==";

  String tmpstr = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAYAAABccqhmAAAABmJLR0QA/wD/AP+gvaeTAAAYkklEQVR4nO3deXRUVZ4H8O99SZGEREiQ1RUIoIRVEBRRUExASHAjsRfHpbtteh1Pj92jM2P3jH3Gtk/3mbanp087M7SNKE2LVlgkG9kg7JKNhEASyAICQiCEBLJXpd6dPwKOC4G6Sb16Sd3v5xz/kbv8DlR96y333QcQEREREREREREREREREREREREREREREREREREREREREREREREREQWGpUuXhixdmjQCgLC7FiJv8IPaR0uWJA0zg+WPAHwLwO0ADADnAfmRgFidlZa8194KiXrGAOiDxfFJswGZKoHR12hWCYF3DLe5NjNz4xm/FUfkBQZALy1KSLo5CLIYEiO97NIlBbYKiNUXzkSlFhWtcltaIJEXGAC9kJSUFNTULrMgsaiXQzRKSKeQxn9npztLfFockQIGQC/ExSf+CsC/+Gi4cgDvmYM8b+du2tTgozGJvMIAULR4adIiachsdF/s86V2CWwUAu/Mv3vq9tdee8308fhEX8EAULB06dNDuozOg+i+2m+lUwDWSchVOWkbai2eizTGAFAQtyxxNQS+5ccpTUDsE8B7cIeuy8pa2+rHuUkDDAAvxSWsWAYp0mwsoUlAvu8x5Du5KRsLbKyDAggDwAvLly8f3GGGlAEYb3ct3WQFIN41gxxrcre8f9buamjgYgB4ITZhxW+FFP9odx1X4REQ2yXkqgt1wzZzbQGpYgBcR+yypGlCyCIADrtruY4zkFgrguQ7WSkbKu0uhgYGBsB1xC1LzITAYrvrUFQkIFaFBnW+v2XLlma7i7maxfFPTJUy6CVAxkKIMQCCLZxOQqAeEnsg8Kfs1ORcC+caUBgA17B4WWKCFEhR7Td0yA344fe+je079qCg6AA8Ho8V5XmjFUCykObqrPSNuwBIuwr5vLiEFd+HFH+EtV/6ngn8af7dU1/kWgsGQI9mz17pGDb6wkEAd6r2ffmlv0fsogUAgAuNTcjdthOZOdtw4uSnvi5TRbWEWOMwPe9mZGw8ZVcRl0N1C2z+7AmJX2SlJ79uZw39AQOgB7EJK74rpFil2m/O7Jn41S9fveqfVVXXInvbDuRu34nm5pY+19hLpoDYZgq5Nkx0JqekpLT5cW4RF594BMBEP87Zk3YzyDFO97soDICrSEpKGtTUJisBjFPpFxISglV/+h3GjB51zXYulxsf5xcifWsODpSWQUrbjsybJOSHBoxVWWnOIqsnWxyfNFtCFlo9j7ekkCtzUjf82e467GTPOVg/19iOlULxyw8A337um9f98gPAoEEOLLh/HhbcPw/15xuwLW8X0jKyUXf2XK/q7YNIAbFSQq6Mi08sB/BesClWZ2Q46y2ZTcg7+8dViG7CVD+9CzRBdhfQ3yQlJQ3qdEsngCEq/aLHj8VLL/4AQqgdVIUPHoypMXfi8UeXYUrMHfB4PDh9pg4ej9+vT40AEGsKvBg9KWbauIlTWp57+qnavLw8n31loydOmQdgua/G6yspUFZbVZFqdx124hHAlzS2y6cFcLNqv+88/zQMo/cPCAohMPuuGZh91wy0trZhx669yN62A4fL/X5LPwTAU4aQT+0pOHQ6Lj5xrREk3s7c4qz2dyFkPQbAFwkh8VPVTtOmxuDuWTN9VkR4+GAseyQWyx6JxScnTiFn2w5k5mxHU9NFn83hpZsAvGJ65Ctx8YlFAmKVazD+lud02nYFk3yLpwCfE5ewYhkgfqLSRwiBn//TSxh+4zBLaoocOgSzZk7HvXNmIipyCNxuF843NFoy13XcBGB5kBs/jr4jJnrCxJiGmqrykyoDRE+aMhv96BQAQBFPAehzxI9Ve9x/3z24Y9IEK4r5jMvViUuXLmLGtMmYMW0yLl1qQUlZOfYXHMCZOr9fOBwCiRck8EJsQuIRA1hvCrE6J8V5wt+FUN8xAC6LjV8xHhJLVPoIIfD8M1+3qqTPnD179gu3CocMicCC+XOxYP5c1B4/ifzCEpSWVaCzs9PyWj5PSNwhgX8TUr4atywpQwSZqxtO35jGh5IGDgbAZUKK70GobfN1z5zZuPUW5euFSjo6OnDxYlOPfz5+7K0YP/ZWrHhsKcorj2Lf/gOoqjnm77UFwRByuTTF8mGjLzTGxq/wyYanC2ePxfMJs7xuv6PoONakFvdlSu0wANC9y+/FNvms6ldmxePxltTzeXV1p71q53AEY8a0GMyYFoPGpos4UHoIez4uQmOj3y8cRgmIlRByZVx8YhGEWGs6uv7KDU/7JwYAgAutiDPENV/u8RXjxt6G6dOmWFUSAKC9vQ0tLeoX3KMih2LRwvl4aMF9qKo5hsLigyg9WAF3V5cFVV7TbEg523AF/SYuPnGLEPKcfYse6WoYAACEkH+n2ifxyUeVF/2oqq/v24I8IQQmTRiPSRPG44lHH0HpwXLs+bgYn572+wuKQgAkmfzy9zvaPwtwebuvcwDCve0zLCoSa1e/BYfDuj1CXC4XqqqOWHIuf+rTOhQUlaCo5BDa2tp9Pv6AIeVxGOIFnfcH0H4dwG0Tpy8XgNIRQPzSOMy92/uLU71x7lwd2tqseVBvyJAITL5jAhbMn4sxo0fB5XKh4ULPFxoDlhCRAJ6NviNmxLPffGqrL5c9DxTanwII4AnVPg8tvN+KUj7j8XjQ2Gj9Yp/g4GDMnB6DmdNjcPHiJeQXHURBUYldC43sI/GjvfmH6gBotz+A1qcAlx/7PQsg0ts+t9xyE1b/zx8srAo4f74edXX2vEhYSona4yeQX1iK0rJyuFza3NLXcn8ArY8AGtvlfULhyw8ADy2w9tcfAJqa7PsFFkIgetztiB53O558dAkOlJYjv6gUxz9RWvU7EIUJ0/UoAK32B9A6AISJxarHQFYf/nd0dKCjo8PSObwVEhKCe+fehXvn3oX6+gYUlx5CfmEpGv3/UJJf6Lg/gNYBAIE4lebR48filpvHWFUNAHt//a9lxIgbsSR2IeIWPYDKIzXYX3gA5ZXVdm546nNSIMLuGvxN2wCIj/9mlAsupUv5s++aYVU5ALrPv/trAFxhGAZiJk9EzOSJaG1tQ2HxQewvLLVjNyPyAW0DwGW45sBUW/s/667pVpUDAGhtbUGX/1fr9Vp4+GAsfOBeLHzgXtSdrUdhcRnyC4vR0qrx2oIBRtsAkKaYIxQ2qBs0yIEpMdaeIjY398t3eHhl9KgRSFi6CI/ELcDhiqMoKD6IispqOzc8JS/0fg+rAc6AnKPSftqUGIQMGmRVOQAGdgBcERzc/VDSC899HQmPPGx3OXQd2gaABGartJ8109rDf5fLBZfLv8/zWy0sLMTuEug6tDwFWLLkyTEmcItKn5jJk6wqBwDQ3HzJ0vEHgnvnzsJTT/ruEeuP84vx4cY0n40XiLQ8AvAEGXNV2gshMH7c7VaVAwC9euyXqK+0DAAhpNLtv5vGjEZYWJhV5QDofvafyN+0DABATFZpPSFa+SVBSlwu14C6/UeBQ9MAUHs5ZfT4sRaV0a29nffNyR46BoCQQLRKh3FjrT3/5+E/2UW7AFi6NGm4AG5Q6TN61EirygEAdHTwCIDsoV0AdAn19/6NHHGjFaV8xt/7+RNdoV0ACCmVHucLDx9s6R0AKSUvAJJttAsAM0ht++/hN1r76+92u7lenmyjXQAYplTaAWjkyOFWlQKgOwCI7KJdAJjCGKzSPipyqFWlAADcbpel4xNdi3YBICCVAiAkxNoHWnj+T3bSMACgFAChFgeAaZqWjk90LdoFABTeAAQAgyzeA4AXAMlO2gWAqXgEEBJibQDwCIDspF0AQIp+dQQQyAGg+ncXanHY0ldpFwBC8JjbX0aNVFtDMXKEtbdc6au0CwAJKD1543JZe5vOMAL37Ww3jRmNEcOHedXW4XBgaoy1uy7RV2kXAIZiAHR2Wh0AgftPIITAYwlLIMT1Qy72ofsREaF0dkY+ELifvp61qjS2+kEdIQL7nyDmzglIfHzZNYPugfvmIPah+X6siq7QblNQ1VMAqwMgkI8Arph3zyyMG3sbduzeh6NVx3CpuQXhg8MwbuytmD9vDiaMt3a/BeqZdgEAxSOADotPAYKCgiwdv78YPWo4vrZiud1l0JcE/s/PlwgplXbfaGpqsqoUAIDDwVtfZB/tAsA0hNI3+lx9g1WlAOi++k1kF+0CQJrijEr7c+fqrSoFgPULjYiuRbsAQLB5WqV5e0cHWlqVLhsoEULwKIBso10AeOQgpQAAgHP1560o5TM8CiC7aBcAeSnvn5eA0mt4rT4NCA219q1DRD3RLgAAQAhUq7SvPXbCqlIAwPLXjhH1RMsAgESVSvOa2mNWVQIACAtTekKZyGf0DADISpXW1TXWBkBISIg2C4Kof9FxJSCEQKHKQ8F1Z8+hpbUVEeHWPawSFhbWb18R7u7qQk3NcRw/cQqnz5zF+YYmXGpuhsvl5p6GA5yWAeAyjQKH8D4BpJSoqTmGGdOnWlZTeHhEvwoAKSWOHK3B/sISlFdWc/vyAKVlAOSlO+vi4hNPAbjF2z4VR6otDYAbbhiCs2frLBvfW1JKHCg9jMzcnai3eBUk2U/LALisAAoBUFxSiq8nPW5ZMaGhoXA4HLb+0p4+U4fkTRk4fuKUbTWQf2kbAEKgQEo84W37Q4cr0dHRidBQ67YJj4i4AY2NFywbvydSSuzaW4CU9Bx4PB6/z0/20fQuAGBKWaDSvqurC2WHyq0qB0D3aYC/eTwevO/8CJtTMvnl15C2AdDl8BQCUPrEFx0otaiabhEREX69Heh2d+HPa9ajsLjMb3P2Z0Ki/1yF9RNtAyBv8+YmAEUqfYoOHLSomm6GYWDIEGvfRXiFaZp4d50TR6tq/TLfwCAq7K7A37QNAAAQkNkq7T85cRInTn5qVTkAgKioKEvHv+KjtGyUVyqtiA50baLLs8XuIvxN6wCQhqEUAACwLW+nFaV8ZvDgcMtfSFpaVoFde/ItnWOgkRBvZGVtOmd3Hf6mdQBcOB21F0CjSp9tebstf5/f0KGRlo3d2tqGDZvTLBt/IBLAH3PSnG/YXYcdtA6AoqJVbgiZqtKn7uw5lFccsaokAEBU1DCv9tLvjbTM7WhpVdoWMXAJHJMCD2WlJb8IQMs3Rmm7DuD/GZsA+YxKj215uzAl5k6rCoLD4cDQoZFoalI6OLmu+voG7C840Jch3ABSIcUWUyI/1HCcSUv7m9dFLo5PWikh/9fb9gtnj8XzCbO8Lm5H0XGsSS32ur2UMjsnbUOe1x0CkPYBECo6MjtkSCsUXhu+Y9c+rHzhOYRYuJPP8OEjfB4A23bu7cvpy3qPGfTzbRkf1PiyJrKX1qcAAJCSktIGYJNKn0vNzcjJ3WFRRd1CQ0MREXGDz8br6OhEccnh3nTthMR3s9OSv8Evf+DRPgAAAAbWqnbZ+FGa5RcDhw8f4bOxDhw83JvnDDxCIjE7PfltnxVC/QoDAEBkqMgFoLRd+MlTnyK/sE/n09cVERHhs+3CyiuUNkHqJsTLWenJShdJaWBhAABwOp0eSKxR7bdhc4oF1XzR6NFj+tRfSoljx0+guvYTxZ6iNDIMf+jT5NTvaX8R8IogM3iVJ6jrZQBeL8YvKT2E6ppjmBA9zrK6wsMjEBFxA1pavN/I2OMxUVVzDAcPVeJw+RE0t6i/10AI8xdO5wY+HRTgGACXbd26/nhsQmKGkEhQ6bdm7Xq8/to/W1UWgO6jgOrqaweA292Fo9W1OFxRhbLDlWhtVXoJ8pc1Dg0zMvsyAA0MDIDPMTx4SxpqAZBfWIzSssOYMW2KVWUhNDQUkZFRX7kt2NHRifLKoyg7fMSn23ZJKbOczmRrX4tM/QID4HOyMpIz4uITSwDMVOn39uq/4r/efMOy1XsAMHLkKFy82ISW1laUV1ajtKwcR6uOWbMppyF6ccWQBiIGwJdIgd8LiXdV+hypqsbO3fuw8IH7LKnpXP157NmXj+07duHI0RrLbz8KE6pXDGmAYgB8SeOZYe8PG9XwOoS4VaXfO++9j/nz7kFwsG829Dh16jR279uP3Xv3o6q61vIv/RcYsPZFCNRvMAC+pKholTs2PunXAvItlX6nz9TBufEjfOOpJ3s9d3VNLXbvy8eevfvxiX0bc55uCRN77Zqc/IsBcBVRg/GXpja8AuB2lX7r1ifj/vvuwa233OxVeyklDlccwZ69+7F7336cPWvtS0i9ISB/sc+ZzMcFNcEAuAqn0+mKW5b4OgT+rNLP5XLjP/7zLfz+t/8Ow7j6GivTNFFeeRQ7d+/Drt370HDBtw/89NFvstI2rLa7CPIfBkAP3G3n1zjCb3wJEJNV+lVUHkVKehYeS3jks//X6XKhqLgEu/fux8f5RWjpxcIcC3UCyBSm+ENWhnOb3cWQfzEAepCXl9cVl7DiZ5BQ3j5n9Zp1mDZ1Mo4fP4k9+/ajoOgAOjo6rSizVyTQDCnTDcPYFGp0pm/ZssX7ZYYUUBgA15CduiE9Lj4pE5BLVPq1d3Tg+z/+mVVl9dYFCJkGKVIMd1h6VtbafnUYQvZgAFyHYZg/NU2xCIDD7lp64QQENktgU1SY2OV0JnNtP30BA+A6MlM2HI6LT3wTwCt21+KlWgikSiGcOSnOPdB0rzvyDgPACy2DxS8j2mQSgPF219KDciHg9HjEh7kZTmvfX0YBhQHghX1OZ/vDy5J+ZAiZYXctl3kkxE5AbjKCgzdnfbT+pN0F0cDEAPBSbrpza1xC4tuQeMGmEjoExG4pkOo28UFeurPOpjoogDAAFAhX2E/gaHtIQkT7aco2KbANgNPhCdmckbHukp/mJU0wABRkZa1tjU1IfEFI5MK67dTqIfARIDcFe9pyMzIy+s8CAgo4DABFOanJeYvjV/xaQrzqw2FPSMithkBqw5kbtxYVrfLNzh5E18EA6IX75kz71z2Fh+ZBYlEfhuHtOrIdA6AXXnvtNXNRQtKzQUIWQ2Kkl90kgAIpxaZgE5u2bnVa+4JBIi8wAHppW6rz09j4rz0o4ElFz+sDPBDYISE2OTyezRkZG217yJ/oahgAfZCT9kHFvKSkqRHt+AGk/JoEJgtgkAC2mwIfSodnS+6mTQ1210nUEwZAH+1zOtsBvHn5P6IBhW8GItIYA4BIYwwAIo0xAIg0xgAg0hgDgEhjDAAijTEAiDTGACDSGAOASGMMACKNMQCINMYAINIYA4BIYwwAIo0xAIg0xgAg0hgDgEhjDAAijTEAiDTGACDSGAOASGMMACKNMQCINMYAINIYA4BIYwwAIo0xAIg0xgAg0hgDgEhjDAAijTEAiDTGACDSGAOASGMMACKNMQCINMYAINIYA4BIYwwAIo0xAIg0xgAg0hgDgEhjDAAijTEAiDTGACDSGAOASGMMACKNMQCINMYAINIYA4BIYwwAIo0xAIg0xgAg0hgDgEhjDAAijTEAiDTGACDSGAOASGMMACKNMQCINMYAINIYA4BIYwwAIo0xAIg0xgAg0hgDgEhjDAAijTEAiDTGACDSGAOASGMMACKNMQCINMYAINIYA4BIYwwAIo0xAIg0xgAg0hgDgEhjDAAijTEAiDTGACDSGAOASGMMACKNMQDIb6SES6V9p8ujNH67y63UXkCtnkDEACC/kTCbVdqfOd+iNP6Z80rDA1Iodgg8DADyGxkkT6i0P1HXhLoG70LA5e5CyZEzavUIcVKpQwBiAJDfDOoKOwLA9La9lBLrMw9Cyuu3Td11BJdaO5XqCYKnUqlDAGIAkN9kZKy7JCQOqfQprarDe+kH4DF7zo2c/Bqk7j6qWo5LusPzVTsFmmC7CyDNCGQBmK7SJa/wGKo+OY/F8yZhyvgRiIwIRUu7C1UnGpBbUIvK4/W9qWN3VtbaVvWOgYUBQH4lpVgHIX+m2u/T+ma8s6XIZ3UIKdf5bLABjKcA5FfZ6c4SALttLqPBNdj40OYa+gUGAPmdhHzd5hLezHM61e4xBigGAPldTtqGTEiRYsfcArLG3Rrxph1z90cMALJFsPT8EMB5P0/rkYbxnby8NR1+nrffYgCQLTIyNp4ypXgGQJcfp301O8W5w4/z9XtBdhdA+jpWVV4dPXHKSQg8CkBYOZcA/pidlvxzK+cYiBgAZKvaqvKSCZNijgJ4FNZ9Ht/ITkt+2aKxBzRLU5fIWw8vf3KOYRofABjnw2EvSSm/m5O+gbf8esAjAOoXjh2tOH3bzCl/CXIhGAJ3o2+L1CSADzxCPJablvyxj0oMSDwCoH5nUULSzUFS/gMEnoHESIWurUJigydY/i53y4aDlhUYQBgA1G89+OCDwYPCRiyAYS6SMO4G5EQAwwBESqBZAJckxFEhZJkQcrsr1MjhAh8iIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIaCP4PRTwcwtm4NEQAAAAASUVORK5CYII=";
  char[] tmpchar = tmpstr.toCharArray();

  private ServiceFragmentDelegate mDelegate;
  // UI
  private EditText mBatteryLevelEditText;
    private EditText delayval;

    private final OnEditorActionListener mOnEditorActionListener = new OnEditorActionListener() {
    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
      if (actionId == EditorInfo.IME_ACTION_DONE) {
        String newBatteryLevelString = textView.getText().toString();
        // Need to check if the string is empty since isDigitsOnly returns
        // true for empty strings.
        if (!newBatteryLevelString.isEmpty()
            && android.text.TextUtils.isDigitsOnly(newBatteryLevelString)) {
          int newBatteryLevel = Integer.parseInt(newBatteryLevelString);
          if (newBatteryLevel <= BATTERY_LEVEL_MAX) {
            setBatteryLevel(newBatteryLevel, textView);
          } else {
            Toast.makeText(getActivity(), R.string.batteryLevelTooHigh, Toast.LENGTH_SHORT)
                .show();
          }
        } else {
          Toast.makeText(getActivity(), R.string.batteryLevelIncorrect, Toast.LENGTH_SHORT)
              .show();
        }
      }
      return false;
    }
  };


  private SeekBar mBatteryLevelSeekBar;
  private final OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      if (fromUser) {
        setBatteryLevel(progress, seekBar);
      }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
  };

  private final OnClickListener mNotifyButtonListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      mDelegate.sendNotificationToDevices(mBatteryLevelCharacteristic);
    }
  };


  private final OnClickListener string_getter = new OnClickListener() {
    @Override
    public void onClick(View v) {
        Integer data;
        StringBuffer stringBuffer = new StringBuffer();
        InputStream inputStream = getResources().openRawResource(R.raw.sample);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      int x = Integer.parseInt(delayval.getText().toString());
      x = x/10;
        if (inputStream != null){
            try{
                while((data = bufferedReader.read()) > 0){
                        try {
                            Thread.sleep(x);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                  mBatteryLevelCharacteristic.setValue(data,
                            BluetoothGattCharacteristic.FORMAT_UINT8, /* offset */ 0);
                    mDelegate.sendNotificationToDevices(mBatteryLevelCharacteristic);
                }

                inputStream.close();

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
  };







  private final OnClickListener string_sender = new OnClickListener() {
    @Override
    public void onClick(View v) {
        int x = Integer.parseInt(delayval.getText().toString());
        x = x/10;

      for(int i = 0; i < tmpchar.length; i ++) {
        int val = tmpchar[i];
        try {
          //set time in mili
          Thread.sleep(x);

        }catch (Exception e){
          e.printStackTrace();
        }
        mBatteryLevelCharacteristic.setValue(val,
                BluetoothGattCharacteristic.FORMAT_UINT8, /* offset */ 0);
        mDelegate.sendNotificationToDevices(mBatteryLevelCharacteristic);
      }
    }
  };



  // GATT
  private BluetoothGattService mBatteryService;
  private BluetoothGattCharacteristic mBatteryLevelCharacteristic;

  public BatteryServiceFragment() {
    mBatteryLevelCharacteristic =
        new BluetoothGattCharacteristic(BATTERY_LEVEL_UUID,
            BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ);

    mBatteryLevelCharacteristic.addDescriptor(
        Peripheral.getClientCharacteristicConfigurationDescriptor());

    mBatteryLevelCharacteristic.addDescriptor(
        Peripheral.getCharacteristicUserDescriptionDescriptor(BATTERY_LEVEL_DESCRIPTION));

    mBatteryService = new BluetoothGattService(BATTERY_SERVICE_UUID,
        BluetoothGattService.SERVICE_TYPE_PRIMARY);
    mBatteryService.addCharacteristic(mBatteryLevelCharacteristic);
  }

  // Lifecycle callbacks
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_battery, container, false);

    mBatteryLevelEditText = (EditText) view.findViewById(R.id.textView_batteryLevel);
    mBatteryLevelEditText.setOnEditorActionListener(mOnEditorActionListener);
    mBatteryLevelSeekBar = (SeekBar) view.findViewById(R.id.seekBar_batteryLevel);
    mBatteryLevelSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    Button notifyButton = (Button) view.findViewById(R.id.button_batteryLevelNotify);
    notifyButton.setOnClickListener(mNotifyButtonListener);

    Button stringButton = (Button) view.findViewById(R.id.send_string);
    stringButton.setOnClickListener(string_sender);

    Button getString = (Button) view.findViewById(R.id.getstring);
    getString.setOnClickListener(string_getter);


    delayval = (EditText) view.findViewById(R.id.delay);


      setBatteryLevel(INITIAL_BATTERY_LEVEL, null);
    return view;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mDelegate = (ServiceFragmentDelegate) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
          + " must implement ServiceFragmentDelegate");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mDelegate = null;
  }

  public BluetoothGattService getBluetoothGattService() {
    return mBatteryService;
  }

  @Override
  public ParcelUuid getServiceUUID() {
    return new ParcelUuid(BATTERY_SERVICE_UUID);
  }

  private void setBatteryLevel(int newBatteryLevel, View source) {
      //where battery level is sent in!!!!!!!!
      newBatteryLevel = (int)'a';
    mBatteryLevelCharacteristic.setValue(newBatteryLevel,
                    BluetoothGattCharacteristic.FORMAT_UINT8, /* offset */ 0);

    if (source != mBatteryLevelSeekBar) {
      mBatteryLevelSeekBar.setProgress(newBatteryLevel);
    }
    if (source != mBatteryLevelEditText) {
      mBatteryLevelEditText.setText(Integer.toString(newBatteryLevel));
    }
  }





  @Override
  public void notificationsEnabled(BluetoothGattCharacteristic characteristic, boolean indicate) {
    if (characteristic.getUuid() != BATTERY_LEVEL_UUID) {
      return;
    }
    if (indicate) {
      return;
    }
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(getActivity(), R.string.notificationsEnabled, Toast.LENGTH_SHORT)
            .show();
      }
    });
  }
  @Override
  public void notificationsDisabled(BluetoothGattCharacteristic characteristic) {
    if (characteristic.getUuid() != BATTERY_LEVEL_UUID) {
      return;
    }
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(getActivity(), R.string.notificationsNotEnabled, Toast.LENGTH_SHORT)
            .show();
      }
    });
  }
}
