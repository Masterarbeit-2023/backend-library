import yaml

with open("config.yaml", "r") as stream:
    try:
        yaml_file = yaml.safe_load(stream)
        print(yaml_file)
    except yaml.YAMLError as exc:
        print(exc)