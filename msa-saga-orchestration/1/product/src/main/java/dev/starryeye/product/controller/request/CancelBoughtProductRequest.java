package dev.starryeye.product.controller.request;

import dev.starryeye.product.application.command.CancelBoughtProductCommand;

public record CancelBoughtProductRequest(
        String boughtId
) {

    public CancelBoughtProductCommand toCommand() {
        return new CancelBoughtProductCommand(boughtId);
    }
}
