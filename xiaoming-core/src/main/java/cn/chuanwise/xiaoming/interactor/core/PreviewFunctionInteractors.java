package cn.chuanwise.xiaoming.interactor.core;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.NonNext;
import cn.chuanwise.xiaoming.annotation.Permission;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.property.PropertyType;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import cn.chuanwise.xiaoming.util.CommandWords;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.*;
import java.util.*;

public class PreviewFunctionInteractors extends SimpleInteractors {
    static final String FILE = "(文件|file)";
    static final String DIRECTORY = "(文件夹|directory)";
    static final String CONTENT = "(目录|content)";
    static final String CD = "(进入|cd)";
    static final String LS = "(列举|ls|dir)";
    static final String CURRENT = "(当前|current|cur)";
    static final String DOWNLOAD = "(获取|下载|download|get)";
    static final String UPLOAD = "(上传|上载|upload|give)";

    static final PropertyType<File> CURRENT_CONTENT_PROPERTY = new PropertyType<>();
    protected File getCurrentPath(XiaomingUser user) {
        return user.getProperty(CURRENT_CONTENT_PROPERTY).orElse(null);
    }

    protected void setCurrentPath(XiaomingUser user, File currentPath) {
        user.setProperty(CURRENT_CONTENT_PROPERTY, currentPath);
    }

    @NonNext
    @Filter(CURRENT + CONTENT)
    @Filter("pwd")
    @Permission("file.pwd")
    public void onPwd(XiaomingUser user) {
        user.sendMessage(getCurrentPath(user).getAbsolutePath());
    }

    public String getDirectoryDetails(File file) {
        if (!file.isDirectory()) {
            return file.getAbsolutePath() + " 不是文件夹";
        } else {
            final File[] files = file.listFiles();
            if (files.length == 0) {
                return file.getAbsolutePath() + " 下无任何文件";
            } else {
                return file.getAbsolutePath() + "：" +
                        "\n" + CollectionUtil.toIndexString(Arrays.asList(files), File::getName);
            }
        }
    }

    @NonNext
    @Filter(CD + " {r:文件路径}")
    @Permission("file.cd")
    public void onCd(XiaomingUser user, @FilterParameter("文件路径") String path) {
        final File elderFile = getCurrentPath(user);
        File currentPath = elderFile;
        // 尝试切换目录
        if (Objects.equals(path, "..")) {
            currentPath = currentPath.getParentFile();
        } else {
            // 先尝试一下看看是不是相对路径
            final File asRelativePath = new File(currentPath, path);
            if (asRelativePath.isDirectory()) {
                currentPath = asRelativePath;
            }

            if (elderFile == currentPath) {
                final File asAbsolutePath = new File(path);
                if (asAbsolutePath.isDirectory()) {
                    currentPath = asAbsolutePath;
                }
            }
        }

        if (elderFile == currentPath || Objects.isNull(currentPath)) {
            user.sendWarning("没有切换当前目录");
        } else {
            setCurrentPath(user, currentPath);
            user.sendMessage(getDirectoryDetails(currentPath));
        }
    }

    @NonNext
    @Filter(LS)
    @Permission("file.ls")
    public void onLs(XiaomingUser user) {
        user.sendMessage(getDirectoryDetails(getCurrentPath(user)));
    }

    @NonNext
    @Filter(LS + " {r:路径}")
    @Permission("file.ls")
    public void onLs(XiaomingUser user, @FilterParameter("路径") String path) {
        final File currentPath = getCurrentPath(user);
        File targetFile = currentPath;
        // 尝试切换目录
        if (Objects.equals(path, "..")) {
            targetFile = targetFile.getParentFile();
        } else {
            // 先尝试一下看看是不是相对路径
            final File asRelativePath = new File(targetFile, path);
            if (asRelativePath.isDirectory()) {
                targetFile = asRelativePath;
            }

            if (currentPath == targetFile) {
                final File asAbsolutePath = new File(path);
                if (asAbsolutePath.isDirectory()) {
                    targetFile = asAbsolutePath;
                }
            }
        }

        if (currentPath == targetFile) {
            user.sendWarning("没有招待该目录");
        } else {
            user.sendMessage(getDirectoryDetails(targetFile));
        }
    }

    @NonNext
    @Filter(CommandWords.ADD + DIRECTORY + " {remain}")
    @Filter(CommandWords.NEW + DIRECTORY + " {remain}")
    @Filter("(make|mk)" + DIRECTORY + " {remain}")
    @Permission("file.mkdir")
    public void onMakeDirectory(XiaomingUser user, @FilterParameter("remain") String path) {
        final File currentPath = getCurrentPath(user);
        final File subDirectory = new File(currentPath, path);
        if (subDirectory.mkdir()) {
            user.sendMessage("创建成功");
        } else {
            user.sendError("创建失败，可能是文件夹已存在或缺少权限");
        }
    }

    @NonNext
    @Filter(CommandWords.ADD + FILE + " {remain}")
    @Filter(CommandWords.NEW + FILE + " {remain}")
    @Filter("(make|mk)" + FILE + " {remain}")
    @Permission("file.mkfile")
    public void onMakeFile(XiaomingUser user, @FilterParameter("remain") String path) {
        final File currentPath = getCurrentPath(user);
        final File subFile = new File(currentPath, path);
        boolean success;
        try {
            success = subFile.createNewFile();
        } catch (IOException exception) {
            success = false;
        }
        if (success) {
            user.sendMessage("创建成功");
        } else {
            user.sendError("创建失败");
        }
    }

    @NonNext
    @Filter(UPLOAD + FILE + " {remain}")
    @Permission("file.upload")
    public void onGetFile(XiaomingUser user, @FilterParameter("remain") String path) {
        final GroupContact groupContact;
        user.sendMessage("需要将该文件提取到哪个群文件中？");
        final File currentPath = getCurrentPath(user);
        final File target = new File(currentPath, path);

        if (!target.isFile()) {
            user.sendError("该文件不存在");
            return;
        }

        try {
            final Contact miraiContact = user.getContact().getMiraiContact();
            try (ExternalResource resource = ExternalResource.create(target)) {

            }
        } catch (IOException exception) {
            user.sendMessage("无法获取该文件，请去后台手动处理");
        }
    }
}