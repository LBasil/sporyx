extends Control

signal fight_request(player_stats)

var combat_log_lines = []
const MAX_LOG_LINES = 100  # Augmenté pour gérer plus de lignes

func _ready():
	$Panel/VBoxContainer/ChallengeButton.pressed.connect(_on_challenge_button_pressed)

func _on_challenge_button_pressed():
	emit_signal("fight_request", {})

func update_combat_log(message):
	# Découper le message en lignes
	var lines = message.split("\n")
	for line in lines:
		if line.strip_edges() != "":
			combat_log_lines.append(line.strip_edges())
	
	# Limiter le nombre de lignes
	if combat_log_lines.size() > MAX_LOG_LINES:
		combat_log_lines = combat_log_lines.slice(combat_log_lines.size() - MAX_LOG_LINES)
	
	# Mettre à jour le RichTextLabel
	$Panel/VBoxContainer/CombatLog.text = "[center]" + "\n".join(combat_log_lines) + "[/center]"
	
	# Faire défiler jusqu'en bas
	$Panel/VBoxContainer/CombatLog.scroll_to_line($Panel/VBoxContainer/CombatLog.get_line_count() - 1)

func update_opponent_stats(hp, attack):
	$Panel/VBoxContainer/OpponentStats.text = "Adversaire : HP " + str(hp) + ", Attaque " + str(attack)
