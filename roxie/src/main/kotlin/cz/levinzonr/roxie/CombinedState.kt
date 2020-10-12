package cz.levinzonr.roxie

interface CombinedState : BaseState {

    val primaryState: BaseState
    var secondaryState: BaseState


}