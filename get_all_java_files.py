import os

yourpath = 'src/main/java'

for root, dirs, files in os.walk(yourpath, topdown=False):
   for name in files:
      print(os.path.join(root, name))