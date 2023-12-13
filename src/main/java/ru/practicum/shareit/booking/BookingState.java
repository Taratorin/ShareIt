package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.BadRequestException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState valueOfState(String stateString) {
        BookingState stateFound = null;
        for (BookingState state : BookingState.values()) {
            if (state.toString().equals(stateString.toUpperCase())) {
                stateFound = state;
            }
        }
        if (stateFound == null) {
            throw new BadRequestException("Unknown state: " + stateString);
        }
        return stateFound;
    }
}
