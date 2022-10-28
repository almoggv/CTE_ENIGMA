package service;

import dto.DecryptionWorkPayload;
import dto.MachineState;

import java.util.ArrayList;
import java.util.List;

public class DecryptionWorkParserService {

    public static List<MachineState> unzip(DecryptionWorkPayload payload){
        List<MachineState> unzippedWork = new ArrayList<>();


        return unzippedWork;
    }

    public static DecryptionWorkPayload zip(List<MachineState> workToDo){
        DecryptionWorkPayload zippedWork = new DecryptionWorkPayload();



        return zippedWork;
    }

}
