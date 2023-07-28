import javalang

def parse_java_file2(filename):
    with open(filename, 'r') as file:
        java_code = file.read()

    # Parse the Java source file
    tree = javalang.parse.parse(java_code)

    # Print package
    print(f"Package: {tree.package.name}\n")

    # Iterate over classes in the file
    for path, node in tree.filter(javalang.tree.ClassDeclaration):
        print(f"Class: {node.name}")

        # Iterate over variables in the class
        for path, field_decl in node.filter(javalang.tree.FieldDeclaration):
            print(f"Variable: {field_decl.declarators[0].name}, Type: {field_decl.type.name}, "
                  f"Visibility: {field_decl.modifiers}")
            if field_decl.declarators[0].initializer is not None:
                print(f"Initial Value: {field_decl.declarators[0].initializer.value}")

        # Iterate over methods in the class
        for path, method_decl in node.filter(javalang.tree.MethodDeclaration):
            print(f"Method: {method_decl.name}, Return type: {method_decl.return_type}, "
                  f"Visibility: {method_decl.modifiers}")
            for parameter in method_decl.parameters:
                print(f"Parameter: {parameter.name}, Type: {parameter.type.name}")

            # Print method body as a string
            if method_decl.body and method_decl.body:
                #body_start = method_decl.body[0].position[0] - 1
                body_start = method_decl.body[0].position[0] - 1
                method_body_lines = java_code.split('\n')
                brackets_count = 1
                body_end = body_start
                while brackets_count > 0:
                    brackets_count += method_body_lines[body_end].count('{')
                    brackets_count -= method_body_lines[body_end].count('}')
                    if brackets_count > 0:
                        body_end += 1
                # using len(java_code.split('\n')) to get the last line
                #body_end = len(java_code.split('\n'))
                method_body = java_code.split('\n')[body_start:body_end]
                print("Method Body: ")
                print('\n'.join(method_body))

        # Iterate over constructors in the class
        for path, constructor_decl in node.filter(javalang.tree.ConstructorDeclaration):
            print(f"Constructor: {constructor_decl.name}, Visibility: {constructor_decl.modifiers}")
            for parameter in constructor_decl.parameters:
                print(f"Parameter: {parameter.name}, Type: {parameter.type.name}")

            # Print constructor body as a string
            if constructor_decl.body and constructor_decl.body:
                body_start = constructor_decl.body[0].position[0] - 1
                constructor_body_lines = java_code.split('\n')
                brackets_count = 1
                body_end = body_start
                while brackets_count > 0:
                    brackets_count += constructor_body_lines[body_end].count('{')
                    brackets_count -= constructor_body_lines[body_end].count('}')
                    if brackets_count > 0:
                        body_end += 1
                # using len(java_code.split('\n')) to get the last line
                #body_end = len(java_code.split('\n'))
                constructor_body = java_code.split('\n')[body_start:body_end]
                print("Constructor Body: ")
                print('\n'.join(constructor_body))

file_path = 'src/main/java/com/example/backend/Test.java'

parse_java_file2(file_path)



