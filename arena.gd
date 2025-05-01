extends Control

signal fight_request(player_stats)

func _ready():
    $Panel/VBoxContainer/ChallengeButton.pressed.connect(_on_challenge_button_pressed)

func _on_challenge_button_pressed():
    # Stats du joueur
    var player_stats = {"hp": 10, "attack": 3}
    # Réinitialiser le log
    $Panel/VBoxContainer/CombatLog.text = "Combat en cours...\n"
    # Envoyer une demande de combat au serveur
    emit_signal("fight_request", player_stats)

func update_response(response):
    $Panel/VBoxContainer/CombatLog.text = response

func update_opponent_stats(hp, attack):
    $Panel/VBoxContainer/OpponentStats.text = "Adversaire : HP " + str(hp) + ", Attaque " + str(attack)