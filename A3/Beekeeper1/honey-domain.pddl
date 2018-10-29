;; Domain definition
(define (domain honey-domain)
    ;; Predicates: Properties of objects that we are interested in (boolean)
    (:predicates
        (POSITION ?x) ; True if x is a position
        (ARM ?x) ; True if x is an arm
        (OBJECT ?x) ; True if x is an object
        (BEE-HOUSE ?x) ; True if x is a bee-house
        (HONEY-POT ?x) ; True if x is a honey pot
        (COSTUME ?x) ; True if x is a costume
        (person-at ?x) ; True if the person is at x
        (object-at ?x ?y) ; True if x is an object (bee-house, honey-pot or costume), y is a position and x is in y
        (free ?x) ; True if x is an arm and there is NO honey pot in it
        (hold ?x ?y) ; True if x is an arm, y is a honey pot and x holds y
        (wearing ?x) ; True if x is a costume and the person is wearing it
        (empty ?x) ; True if x is a honey pot and it is empty
        (has-honey ?x) ; True if the bee-house x has honey
    )

    ;; Actions: Ways of changing the state of the world
    ; The person can move from position x to position y
    ; Parameters:
    ; - x: the position from which the person starts moving
    ; - y: the position to which she moves
    (:action move
        :parameters  (
            ?from
            ?to
        )
        :precondition (and  
            (POSITION ?from) 
            (POSITION ?to) 
            (person-at ?from)
            (road ?from ?to)
        )
        :effect (and 
            (person-at ?to) 
            (not (person-at ?from))
        )
    )

    ; The person can take a costume x and put it on if she and the costume are both at position y
    ; Parameters:
    ; - x: the costume
    ; - y: the position where the person will put on the costume
    (:action wear-at
        :parameters (
            ?costume 
            ?position
        )
        :precondition (and 
            (COSTUME ?costume)
            (POSITION ?position)
            (object-at ?costume ?position)
            (person-at ?position)
            (not (wearing ?costume))
        )
        :effect (and 
            (wearing ?costume)
            (not (object-at ?costume ?position))
        )
    )

    ; The person can take off the costume x at position y
    ; Parameters:
    ; - x: the costume
    ; - y: the position where the costume will be taken off
    (:action take-off-at
        :parameters (
            ?costume 
            ?position
        )
        :precondition(and 
            (COSTUME ?costume)
            (POSITION ?position)
            (wearing ?costume)
            (person-at ?position)
        )
        :effect (and 
            (not (wearing ?costume))
            (object-at ?costume ?position)
        )
    )

    ; The person can collect all honey from bee house w to honey pot x if 
    ; there is honey in the bee house, the honey pot is empty, 
    ; the person is wearing costume y, has a free arm v, and 
    ; she, the bee house and the honey pot are in the same position z.
    ; - x: the honey pot
    ; - y: the costume
    ; - z: the position where the person collects the honey
    ; - w: the bee house
    ; - v: the arm
    (:action collect
        :parameters (
            ?honeypot
            ?costume 
            ?position 
            ?beehouse 
            ?arm
        )
        :precondition (and 
            (BEE-HOUSE ?beehouse)
            (HONEY-POT ?honeypot)
            (COSTUME ?costume)
            (POSITION ?position)
            (ARM ?arm)
            (has-honey ?beehouse)
            (empty ?honeypot)
            (wearing ?costume)
            (free ?arm)
            (person-at ?position)
            (object-at ?honeypot ?position)
            (object-at ?beehouse ?position)
        )
        :effect (and 
            (not (has-honey ?beehouse))
            (not (empty ?honeypot))
        )
    )

    ; The person can pick-up honey pot x with free arm y at position z
    ; - x: the honey pot
    ; - y: the arm
    ; - z: the position where the object is picked up
    (:action pick-up
        :parameters (
            ?honeypot 
            ?arm 
            ?position
        )
        :precondition (and 
            (HONEY-POT ?honeypot)
            (ARM ?arm)
            (POSITION ?position)
            (person-at ?position)
            (object-at ?honeypot ?position)
            (not (hold ?arm ?honeypot))
            (free ?arm)
        )
        :effect (and 
            (hold ?arm ?honeypot)
            (not (object-at ?honeypot ?position))
            (not (free ?arm))
        )
    )

    ; The person can drop off honey pot x that she holds in arm y at position z
    ; - x: the honey pot
    ; - y: the arm
    ; - z: the position where the object is dropped off
    (:action drop-off
        :parameters (
            ?honeypot 
            ?arm 
            ?position
        )
        :precondition (and 
            (HONEY-POT ?honeypot)
            (POSITION ?position)
            (ARM ?arm)
            (not (free ?arm))
            (person-at ?position)
            (hold ?arm ?honeypot)
        )
        :effect (and
            (free ?arm)
            (not (hold ?arm ?honeypot))
            (object-at ?honeypot ?position)
        )      
    )
)