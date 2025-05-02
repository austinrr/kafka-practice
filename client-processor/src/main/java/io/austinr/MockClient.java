package io.austinr;

import io.austinr.records.ReservationRecord;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MockClient {

    private static final List<String> Lots = List.of(
            "Red lot",
            "Harry's Lot",
            "EzPark",
            "SafeLot",
            "BlueLot",
            "CityLot",
            "Fancy Lot"
    );

    public MockClient() {}

    public static ReservationRecord makeFakeRecord() {
        return new ReservationRecord(
                generatePlate(),
                new Random().nextInt(1,1000),
                Lots.get(new Random().nextInt(0,Lots.size()-1))
        );
    }

    private static String generatePlate() {
        int plateLen = new Random().nextInt(6,7);
        int lettersLen = new Random().nextInt(1,5);
        char[] plate = new char[plateLen];
        char[] character = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

        for (int i = 0; i < plateLen; i++) {
            if (lettersLen > 0 && new Random().nextInt() > 0) {
                plate[i] = character[new Random().nextInt(character.length)];
                lettersLen--;
            } else {
                plate[i] = Integer.toString(new Random().nextInt(0,9)).charAt(0);
            }
        }

        return Arrays.toString(plate);
    }
}
