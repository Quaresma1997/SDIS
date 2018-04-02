echo -n "Protocol version\n> "
read protocol_version
echo -n "Peer identifier\n> "
read peer_id
echo -n "Service Access Point\n> "
read service_acces_point

java communication/Peer $protocol_version $peer_id $service_acces_point 224.0.0.0:8000 224.0.0.0:8001 224.0.0.0:8002

PAUSE