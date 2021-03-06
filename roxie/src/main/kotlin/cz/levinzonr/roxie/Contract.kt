package cz.levinzonr.roxie

interface Contract {
    val state: BaseState
    val event: BaseEvent
}