package io.abc_def.kickstart_fx.comp.base;

import io.abc_def.kickstart_fx.comp.SimpleComp;

import lombok.Getter;

@Getter
public abstract class ModalOverlayContentComp extends SimpleComp {

    protected ModalOverlay modalOverlay;

    void setModalOverlay(ModalOverlay modalOverlay) {
        this.modalOverlay = modalOverlay;
    }
}
