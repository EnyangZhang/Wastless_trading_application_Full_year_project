def main():
    with open("country_list.txt", "r") as countries:
        country_lines = countries.readlines()

        all_names = []
        for line in country_lines:
            line = line.strip()
            columns = line.split(',')
            names = columns[6:-1]
            all_names += names

        no_slash_names = []
        for name in all_names:
            if "/" in name:
                split_names = name.split("/")
                for split_name in split_names:
                    no_slash_names.append(split_name)
            else:
                no_slash_names.append(name)

        no_bracket_names = []
        for name in no_slash_names:
            if "(" in name:
                no_bracket_names.append(name.split("(")[0])
            else:
                no_bracket_names.append(name)

    output_string = "export const countries = [\n"
    for name in no_bracket_names[:-1]:
        if len(name) > 0:
            output_string += '"' + name.replace('"', '').strip() + '",\n'
    output_string += "];"
    with open("country_names.js", 'w') as output:

        output.write(output_string)


main()
