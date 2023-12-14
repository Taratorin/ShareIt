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
        for (BookingState state : BookingState.values()) {
            if (state.toString().equalsIgnoreCase(stateString)) {
                return state;
            }
        }
        throw new BadRequestException("Unknown state: " + stateString);
    }
}
