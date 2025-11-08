init -1 python:
    import os

    def dump_all_files(destination="/tmp/game_dump"):
        """
        Extracts all files known to renpy.list_files() and writes them to the destination folder.
        Compatible with older Ren'Py Python runtimes.
        """
        if not os.path.exists(destination):
            os.makedirs(destination)

        total = 0

        for f in renpy.list_files():
            try:
                data = renpy.loader.load(f).read()
                out_path = os.path.join(destination, f)
                dir_name = os.path.dirname(out_path)
                if not os.path.exists(dir_name):
                    os.makedirs(dir_name)

                with open(out_path, "wb") as out_file:
                    out_file.write(data)

                total += 1

            except Exception as e:
                renpy.log("[auto_extract_all_files] Failed: {} -> {}".format(f, e))

        renpy.log("[auto_extract_all_files] Done. {} files extracted to {}".format(total, destination))
        return total


label before_main_menu:
    # Run automatically before main menu
    $ total = dump_all_files("/tmp/game_dump")
    "✅ Extracted [total] files to /tmp/game_dump."
    $ renpy.quit()
