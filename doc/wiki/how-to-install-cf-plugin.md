The goal of this page is to explain Credibility Framework plugin installation.

[Go back to Wiki home page](home)

## Contents

[[_TOC_]]

## Obtain Credibility Framework plugin
[Go back to Contents](#contents)

The plugin builds of the Credibility Framework can be found at branch [develop/build](https://gitlab.com/iwf/cf/tree/develop/build) folder

## Install Credibility Framework plugin
[Go back to Contents](#contents)

1. Download the plugin build 

2. Launch Eclipse

3. Go to `Help` menu, then click on `Install New Software`

![2019-07-11_help-menu_install-new-software](uploads/6e88a9bf7c7a083882e0bb52907bb7d3/2019-07-11_help-menu_install-new-software.png)

4. In the opened window, click on `Add` button

![2019-07-11_install-window_add](uploads/7e051f58071b6f51c34e80fbc0d267ef/2019-07-11_install-window_add.png)

5. Click on `Local` button and select the plugin build folder

![2019-07-11_install-window_local](uploads/1bc99809a64ff460f38c5307a14660c8/2019-07-11_install-window_local.png)

![image](uploads/4742e42a9019101dbc0cc7e5011cf7e8/image.png)

6. Name the local repository as you want and click on `Ok`

![image](uploads/bf6aa94e1c64f4daf22de27ae51d1ef8/image.png)

7. Select `Credibility Feature` and click `Next`

![image](uploads/b11fde7c089074731b645129a437ca86/image.png)

8. In a disconnected environment, disable the option to contact update sites:

- Solution 1, `disable contacting the update sites` during installation phase:

![image](uploads/fba97bee92e5036b5e6c3c8b6f364b1e/image.png)

- Solution 2, `disable the other update sites`:

Go to `Window > Preferences`, then search for `Install/Update > Available Software Sites` and uncheck all the other update sites except the cf one:

![image](uploads/6c76233d3318cf02130ed5aabd692a56/image.png)

![image](uploads/269bcca400390b224a61235851466b59/image.png)

https://stackoverflow.com/questions/11219215/eclipse-hangs-when-installing-new-software

9. Click `Next` again

![2019-07-11_install-window_next](uploads/ec66b605f0d63aa557cb79007f781482/2019-07-11_install-window_next.png)

10. Check `I accept the terms of the license agreement` and click `Finish`

![2019-07-11_install-window_accept-license](uploads/9c8175bbdf9ff8af9b762e96354b290f/2019-07-11_install-window_accept-license.png)

11. Click `Install anyway` in Security Warning window

![2019-07-11_security-warning](uploads/f297be1625897fd1735c3170134ad50b/2019-07-11_security-warning.png)

12. Click `Restart Now`. Eclipse will restart with your plugin installed

![2019-07-11_restart](uploads/ab88f0d44825cdd0876429e148bfedae/2019-07-11_restart.png)

## Check Credibility Framework plugin installation
[Go back to Contents](#contents)

1. Launch Eclipse

2. Go to `Help` menu, then click on `About Eclipse`

![2019-07-11_help-menu_about](uploads/e8d59b97ca38fff00d92fb0b4ae15474/2019-07-11_help-menu_about.png)

3. Click `Installation Details`

![2019-07-11_installation-details](uploads/0f90965b468ad6be658f90b4e8de81e0/2019-07-11_installation-details.png)

4. Find `Credibility Feature` in current window, or type `Credibility Feature` in search bar

![image](uploads/0e5b3e168c78a2a20978520256c8fcef/image.png)

## Update Credibility Framework plugin
[Go back to Contents](#contents)

1. Click on menu `Help > Check for updates`:

![image](uploads/4d196b81b8379bf260658d29c1058a3e/image.png)

Eclipse will check the update sites:

![image](uploads/8199a887335de901adf4d9e17e0e3597/image.png)

2. Check `Credibility Framework` and click `Next`:

![image](uploads/6c2af8ce6714919d92222ab3cc79bd29/image.png)

3. Click `Next`:

![image](uploads/f872eaa20cca131e81ce0f478036d8ab/image.png)

4. Accept License and click `Finish`:

![image](uploads/e9ffb89cf2f9bf4f1a08f163f447f71d/image.png)

5. Click `Install anyway`:

![image](uploads/4b78e21e545872b3ff3b9c44f848c0ef/image.png)

3. Click `Restart Now`:

![image](uploads/cbcd52f4eaf8b7d2bd2286f243f5b0f5/image.png)

Your plugin is updated.

## Uninstall Credibility Framework plugin
[Go back to Contents](#contents)

1. Launch Eclipse

2. Go to `Help` menu, then click on `About Eclipse`

![2019-07-11_help-menu_about](uploads/e8d59b97ca38fff00d92fb0b4ae15474/2019-07-11_help-menu_about.png)

3. Click `Installation Details`

![2019-07-11_installation-details](uploads/0f90965b468ad6be658f90b4e8de81e0/2019-07-11_installation-details.png)

4. Select `Credibility Feature` in current window and click `Uninstall`. You can search plugin by typing `Credibility Feature` in search bar

![image](uploads/cdb474b701f71f5f3c19ac83bd12088f/image.png)

5. Click on `Finish` to confirm uninstallation

![2019-07-11_uninstall_confirm](uploads/3ca0b3759975d6bbc1d5c8937b82de0b/2019-07-11_uninstall_confirm.png)

6. Click `Restart Now`. Eclipse will restart with your plugin uninstalled

![2019-07-11_restart](uploads/23e96f49ccb5b81227bc6ac5913df0f4/2019-07-11_restart.png)


## Dependencies

### ARG

CF is using ARG (Automatic Report Generator) project to build and generate reports. See [Credibility Report](functional-specifications#credibility-report) feature description in the functional specifications.

See [ARG Wiki page](https://gitlab.com/AutomaticReportGenerator/arg/-/wikis/home) and/or [ARG documentation page](https://automaticreportgenerator.gitlab.io/arg/) for furthermore informations about how to install ARG.

[Go back to the top of the page](#content-body)