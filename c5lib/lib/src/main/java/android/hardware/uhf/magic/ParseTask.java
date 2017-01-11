package android.hardware.uhf.magic;

import android.os.AsyncTask;
import android.util.Log;
import java.util.Arrays;

/**
 * @author Dmitriy Logachev
 * Created by koputo on 28.12.16.
 */
class ParseTask extends AsyncTask<UHFCommand, Void, UHFCommandResult> {
    private static final String TAG = "ParseTask";
    boolean resend = false;

    @Override
    protected UHFCommandResult doInBackground(UHFCommand... commands) {
        int buffSize = 1024;
        byte[] buff = new byte[buffSize];
        Arrays.fill(buff, (byte) 0);
        int readed;
        int buffIndex = 0;
        int dataSize;
        int pktStart;
        int pktEnd;
        int commandReturnCode;

        while (true) {
            if (this.isCancelled()) {
                Log.d(TAG, "Tag data read interrupted");
                return new UHFCommandResult(reader.RESULT_TIMEOUT, null);
            }

            readed = reader.Read(buff, buffIndex, buffSize - buffIndex);
            if (readed > 0) {
                Log.d(TAG, "readed: " + reader.BytesToString(buff, buffIndex, readed));
                buffIndex += readed;
                if (buffIndex < 5) {
                    continue;
                }

                pktStart = -1;
                for (int i = 0; i < buffIndex - 4; i++) {
                    if (buff[i] == (byte)0xBB && (buff[i + 1] == (byte)0x01 || buff[i + 1] == (byte)0x02) && buff[i + 3] == (byte)0x00) {
                        pktStart = i;
                        break;
                    }
                }

                if (pktStart < 0) {
                    continue;
                }

                // получаем размер данных в ответе
                dataSize = (((int)buff[pktStart + 3]) << 8) | (int)buff[pktStart + 4];
                pktEnd = pktStart + 5 + dataSize + 2;
                Log.d(TAG, "pktStart = " + pktStart + ", pktEnd = " + pktEnd + ", buffIndex = " + buffIndex);

                // ожидаем когда будет прочитан весь ответ
                if (buffIndex >= pktEnd) {
                    byte parsedCommand = buff[pktStart + 2];
                    Log.d(TAG, "Ответ на команду: " + String.format("%02X", parsedCommand));

                    if (parsedCommand == UHFCommand.Command.ERROR) {

                        /* пробуем в случае записи в метку как-то обработать ситуацию когда пишем
                         * в конец метки и получаем ошибку записи
                         */
                        int errCode = reader.byteToInt(buff, pktStart + 5, 1);
                        Log.d(TAG, "Код ошибки: " + String.format("%02X", errCode));
                        if (errCode == 0xB3 && commands[0].command == UHFCommand.Command.WRITE_TAG_DATA) {
                            // по идее должны вернуть ошибку, но вернём успех
                            return new UHFCommandResult(reader.RESULT_SUCCESS, null);
                        } else {
                            // продолжим попытки отправки команды
                            Arrays.fill(buff, (byte)0);
                            buffIndex = 0;
                            resend = true;
                            continue;
                        }
                    }

                    if (parsedCommand != commands[0].command) {
                        Log.e(TAG, "Разобран ответ на другую команду.");

                        Arrays.fill(buff, (byte)0);
                        buffIndex = 0;
                        resend = true;
                        continue;
                    }

                    // разбираем и возвращаем полученные данные
                    switch (parsedCommand) {
                        case UHFCommand.Command.READ_TAG_ID:
                            return new UHFCommandResult(reader.RESULT_SUCCESS,
                                    reader.BytesToString(buff, pktStart + 5 + 1, dataSize - 3));

                        case UHFCommand.Command.READ_TAG_DATA:
                            Log.d(TAG, "Данные карты прочитаны успешно!");
                            return new UHFCommandResult(reader.RESULT_SUCCESS,
                                    reader.BytesToString(buff, pktStart + 5, dataSize));

                        case UHFCommand.Command.WRITE_TAG_DATA:
                            commandReturnCode = reader.byteToInt(buff, pktStart + 5, 1);
                            Log.d(TAG, "код возврата после записи = " + commandReturnCode);
                            if (commandReturnCode == 0) {
                                Log.d(TAG, "Данные записаны успешно!");
                                return new UHFCommandResult(reader.RESULT_SUCCESS, null);
                            } else {
                                Log.d(TAG, "Не удалось записать данные!");
                                return new UHFCommandResult(reader.RESULT_WRITE_ERROR, null);
                            }

                        case UHFCommand.Command.LOCK_TAG:
                            commandReturnCode = reader.byteToInt(buff, pktStart + 5, 1);
                            Log.d(TAG, "код возврата после блокировки = " + commandReturnCode);
                            if (commandReturnCode == 0) {
                                Log.d(TAG, "Блокировка выполненна успешно!");
                                return new UHFCommandResult(reader.RESULT_SUCCESS, null);
                            } else {
                                Log.d(TAG, "Не удалось выполнить блокировку!");
                                return new UHFCommandResult(reader.RESULT_WRITE_ERROR, null);
                            }

                        case UHFCommand.Command.KILL_TAG:
                            commandReturnCode = reader.byteToInt(buff, pktStart + 5, 1);
                            Log.d(TAG, "код возврата после деактивации = " + commandReturnCode);
                            if (commandReturnCode == 0) {
                                Log.d(TAG, "Деактивация выполненна успешно!");
                                return new UHFCommandResult(reader.RESULT_SUCCESS, null);
                            } else {
                                Log.d(TAG, "Не удалось выполнить деактивацию!");
                                return new UHFCommandResult(reader.RESULT_WRITE_ERROR, null);
                            }

                        default:
                            return new UHFCommandResult(reader.RESULT_ERROR, null);
                    }
                }
            }
        }
    }
}
